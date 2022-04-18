package com.glureau.mermaidksp.compiler

import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.symbol.Visibility
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview

@KotlinPoetKspPreview
class MermaidClassVisitor : KSVisitorVoid() {
    val classes = mutableMapOf<String, MermaidClass>()
    val moduleClasses = mutableSetOf<String>()

    override fun visitFile(file: KSFile, data: Unit) {
        file.declarations.forEach { it.accept(this, Unit) }
    }

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        scan(classDeclaration)
        classDeclaration.declarations.forEach { it.accept(this, Unit) }
        moduleClasses.add(classDeclaration.qualifiedName!!.asString()) // TODO: null to be handled later
    }

    private fun scan(classDeclaration: KSClassDeclaration): MermaidClass {
        val qualifiedName = classDeclaration.qualifiedName!!.asString() // TODO: null to be handled later
        classes[qualifiedName]?.let { return it }
        val klass = MermaidClass(
            qualifiedName = qualifiedName,
            packageName = classDeclaration.packageName.asString(),
            originFile = classDeclaration.containingFile,
            visibility = classDeclaration.getMermaidVisibility(),
            className = classDeclaration.getMermaidClassName(),
            classType = classDeclaration.getMermaidClassType(),
        )
        if (classDeclaration.classKind == ClassKind.ENUM_ENTRY) {
            classDeclaration.declarations
        }
        // Store BEFORE visiting other prop/func so that it can be retrieved from the map (and avoid infinite loop)
        classes[qualifiedName] = klass
        klass.properties =
            classDeclaration.getAllProperties().mapNotNull { it.toMermaidProperty(classDeclaration.classKind) }.toList()
        klass.functions = classDeclaration.getAllFunctions().mapNotNull { it.toMermaidFunction() }.toList()
        klass.supers = classDeclaration.superTypes.mapNotNull { it.getMermaidClass() }.toList()
        klass.inners =
            classDeclaration.declarations.mapNotNull { if (it is KSClassDeclaration) scan(it) else null }.toList()
        return klass
    }

    private fun KSPropertyDeclaration.toMermaidProperty(classKind: ClassKind): MermaidProperty? {
        val decl = type.resolve().declaration
        val qualifiedName = decl.qualifiedName?.asString() ?: return null

        val mermaidClass = if (
            qualifiedName.startsWith("kotlin.") || // TODO: User option to be parameterized?
            qualifiedName.startsWith("java.")
        ) {
            Basic(decl.simpleName.asString())
        } else {
            this.type.getMermaidClass()!!
        }

        val propName = this.simpleName.asString()
        val overrides = (classKind == ClassKind.ENUM_ENTRY) || this.modifiers.contains(Modifier.OVERRIDE) || this.findOverridee() != null
        return MermaidProperty(
            visibility = this.getMermaidVisibility(),
            propName = propName,
            type = mermaidClass,
            overrides = overrides,
        )
    }

    // TODO: User option to be parameterized?
    private val ignoredFunctionNames = listOf("copy", "<init>") + (1..30).map { "component$it" }
    private fun KSFunctionDeclaration.toMermaidFunction(): MermaidFunction? {
        if (this.simpleName.asString() in ignoredFunctionNames) return null
        return MermaidFunction(
            visibility = this.getMermaidVisibility(),
            funcName = this.simpleName.asString(),
            // We could retrieve the name of parameters...
            parameters = this.parameters.mapNotNull { it.type.getMermaidClass() },
            returnType = this.returnType?.getMermaidClass(),
            overrides = this.modifiers.contains(Modifier.OVERRIDE)
        )
    }

    fun KSTypeReference.getMermaidClass(): MermaidClassOrBasic? {
        val decl = resolve().declaration
        val qualifiedName = decl.qualifiedName?.asString() ?: return null
        return if (
            qualifiedName.startsWith("kotlin.") ||  // TODO: User option to be parameterized?
            qualifiedName.startsWith("java.") ||
            decl !is KSClassDeclaration
        ) {
            Basic(toString())
            //Basic(decl.getMermaidClassName())
        } else {
            scan(decl)
        }
    }

    private fun KSDeclaration.getMermaidVisibility(): MermaidVisibility =
        when (getVisibility()) {
            Visibility.PUBLIC -> MermaidVisibility.Public
            Visibility.PRIVATE -> MermaidVisibility.Private
            Visibility.PROTECTED -> MermaidVisibility.Protected
            Visibility.INTERNAL -> MermaidVisibility.Internal
            Visibility.LOCAL -> MermaidVisibility.Private
            Visibility.JAVA_PACKAGE -> MermaidVisibility.Private
        }

    private val functionTypeNames = (0..22).map { "Function$it" }
    private fun KSDeclaration.getMermaidClassName(): String {
        if (this is KSClassDeclaration) {
            if (this.typeParameters.isNotEmpty()) {
                fun Sequence<KSTypeReference>.cleanString(): String {
                    val list = toList()
                    if (list.isEmpty()) return ""
                    return joinToString(
                        prefix = ":"
                    ) { b -> b.element.toString() }
                }
                if (functionTypeNames.contains(simpleName.asString())) {
                    // Not sure if this is required here after all...
                    val returnType = typeParameters.last().getMermaidClassName()
                    val params = typeParameters.dropLast(1).joinToString { tp ->
                        tp.getMermaidClassName() + tp.bounds.cleanString()
                    }
                    return "($params)->$returnType"
                } else {
                    return simpleName.asString() + "~" + typeParameters.joinToString { tp ->
                        tp.getMermaidClassName() + tp.bounds.cleanString()
                    } + "~"
                }
            }
        }
        return simpleName.asString()
    }

    private fun KSClassDeclaration.getMermaidClassType(): MermaidClassType =
        when (classKind) {
            ClassKind.INTERFACE -> MermaidClassType.Interface
            ClassKind.CLASS -> {
                when {
                    modifiers.contains(Modifier.SEALED) -> MermaidClassType.SealedClass
                    modifiers.contains(Modifier.DATA) -> MermaidClassType.DataClass
                    modifiers.contains(Modifier.VALUE) -> MermaidClassType.ValueClass
                    else -> MermaidClassType.Class
                }
            }
            ClassKind.ENUM_CLASS -> MermaidClassType.Enum
            ClassKind.ENUM_ENTRY -> MermaidClassType.EnumEntry
            ClassKind.OBJECT -> MermaidClassType.Object
            ClassKind.ANNOTATION_CLASS -> MermaidClassType.Annotation
        }
}