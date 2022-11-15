/*
 * Copyright 2022 Deezer.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.glureau.k2d.compiler

import com.glureau.k2d.K2DHide
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview

@OptIn(KspExperimental::class)
@KotlinPoetKspPreview
class AggregatorClassVisitor : KSVisitorVoid() {
    val classes = mutableMapOf<String, GClass>()
    val basics = mutableMapOf<String, Basic>()
    val moduleClasses = mutableSetOf<String>()

    override fun visitFile(file: KSFile, data: Unit) {
        file.declarations.forEach { it.accept(this, Unit) }
    }

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        scan(classDeclaration)
        classDeclaration.declarations.forEach { it.accept(this, Unit) }
        moduleClasses.add(classDeclaration.qualifiedName!!.asString()) // TODO: null to be handled later
    }

    private fun scan(classDeclaration: KSClassDeclaration): GClass {
        val qualifiedName = classDeclaration.qualifiedName!!.asString() // TODO: null to be handled later
        classes[qualifiedName]?.let { return it }
        val hide = classDeclaration.getAnnotationsByType(K2DHide::class).toList().isNotEmpty()
        val klass = GClass(
            qualifiedName = qualifiedName,
            packageName = classDeclaration.packageName.asString(),
            originFile = classDeclaration.containingFile,
            isAbstract = classDeclaration.isAbstract(),
            visibility = classDeclaration.getGVisibility(),
            symbolName = classDeclaration.getMermaidClassName(),
            simpleName = classDeclaration.simpleName.asString(),
            classType = classDeclaration.getGClassType(),
            docString = classDeclaration.docString,
            hide = hide,
        )
        if (classDeclaration.classKind == ClassKind.ENUM_ENTRY) {
            classDeclaration.declarations
        }
        // Store BEFORE visiting other prop/func so that it can be retrieved from the map (and avoid infinite loop)
        classes[qualifiedName] = klass
        klass.properties =
            classDeclaration.getAllProperties().mapNotNull { it.toGProperty(classDeclaration.classKind) }.toList()
        klass.functions = classDeclaration.getAllFunctions().mapNotNull { it.toGFunction() }.toList()
        klass.supers = classDeclaration.superTypes.mapNotNull { it.getGClass() }
            // Because KSP consider now that every class extends Any, but we don't care about that in graphs.
            .filter { it.qualifiedName != "kotlin.Any" }
            .toList()
        klass.inners =
            classDeclaration.declarations.mapNotNull { if (it is KSClassDeclaration) scan(it) else null }.toList()

        /*
        classDeclaration.typeParameters.map {
            it.bounds.firstOrNull()?.getGClass()
            val declaration: KSDeclaration? = it.bounds.firstOrNull()?.resolve()?.declaration
            val foo = if (declaration is KSClassDeclaration) {
                scan(declaration)
            } else null
            Logger.warn("Class: ${klass.symbolName} => $foo", it)
        }
         */
        // TODO: Apply same compute on other "generics" computation
        // TODO: rework "symbolName" logic and use a specific mermaid method to display "~
        klass.generics = classDeclaration.generics()
        //     fun KSTypeReference.getMermaidClass(): GClassOrBasic? {
        /*klass.generics = classDeclaration.typeParameters.map {
            scan(it.bounds.first().element)
        }*/
        return klass
    }

    fun KSDeclaration.generics(): List<Generics> {
        return typeParameters.map {
            Generics(
                it.name.asString(),
                it.bounds.first().getGClass()!!
            )
        }
    }

    @OptIn(KspExperimental::class)
    private fun KSPropertyDeclaration.toGProperty(classKind: ClassKind): GProperty? {
        val decl = type.resolve().declaration
        val qualifiedName = decl.qualifiedName?.asString() ?: return null

        val mermaidClass = if (
            qualifiedName.startsWith("kotlin.") || // TODO: User option to be parameterized?
            qualifiedName.startsWith("java.")
        ) {
            Basic(
                qualifiedName = qualifiedName,
                packageName = decl.packageName.asString(),
                symbolName = decl.simpleName.asString(),
                simpleName = decl.simpleName.asString(),
            ).apply {
                generics = decl.generics()

                if (symbolName == "List") {
                    //Logger.error("XXX - " + this.toString(), this@toMermaidProperty)
                }
            }
        } else {
            this.type.getGClass()!!
        }

        val propName = this.simpleName.asString()
        val overrides =
            (classKind == ClassKind.ENUM_ENTRY) || this.modifiers.contains(Modifier.OVERRIDE) || this.findOverridee() != null


        val hide = getAnnotationsByType(K2DHide::class).toList().isNotEmpty()
        return GProperty(
            visibility = this.getGVisibility(),
            propName = propName,
            type = LocalType(type = mermaidClass, usedGenerics = type.resolve().arguments.map { it.type.toString() }),
            overrides = overrides,
            docString = this.docString,
            hasBackingField = this.hasBackingField,
            hide = hide,
        )
    }

    // TODO: User option to be parameterized?
    private val ignoredFunctionNames = listOf("copy", "<init>") + (1..30).map { "component$it" }
    private fun KSFunctionDeclaration.toGFunction(): GFunction? {
        if (this.simpleName.asString() in ignoredFunctionNames) return null
        val params = this.parameters.mapNotNull {
            GFunctionParameter(
                type = it.type.getGClass() ?: return@mapNotNull null,
                usedGenerics = it.type.element?.typeArguments?.map { it.toString() }.orEmpty(),
            )
        }
        val returnType = this.returnType?.getGClass()?.let { type ->
            LocalType(
                type = type,
                usedGenerics = this.returnType!!.element?.typeArguments?.map { it.toString() }.orEmpty()
            )
        }
        val hide = getAnnotationsByType(K2DHide::class).toList().isNotEmpty()
        return GFunction(
            visibility = this.getGVisibility(),
            funcName = this.simpleName.asString(),
            // We could retrieve the name of parameters...
            //usedGenerics = type.resolve().arguments.map { it.type.toString() },
            parameters = params,
            returnType = returnType,
            overrides = this.modifiers.contains(Modifier.OVERRIDE),
            docString = this.docString,
            hide = hide,
        )
    }

    fun KSTypeReference.getGClass(): GClassOrBasic? {
        val decl = resolve().declaration
        val qualifiedName = decl.qualifiedName?.asString() ?: return null
        return if (
            qualifiedName.startsWith("kotlin.") ||  // TODO: User option to be parameterized?
            qualifiedName.startsWith("java.") ||
            decl !is KSClassDeclaration
        ) {
            basics[qualifiedName]?.let { return it }
            Basic(
                qualifiedName = qualifiedName,
                packageName = decl.packageName.asString(),
                symbolName = decl.simpleName.asString(),
                simpleName = decl.simpleName.asString(),
            ).apply {
                basics[qualifiedName] = this
                generics = decl.generics()
            }
        } else {
            scan(decl)
        }
    }

    private fun KSDeclaration.getGVisibility(): GVisibility =
        when (getVisibility()) {
            Visibility.PUBLIC -> GVisibility.Public
            Visibility.PRIVATE -> GVisibility.Private
            Visibility.PROTECTED -> GVisibility.Protected
            Visibility.INTERNAL -> GVisibility.Internal
            Visibility.LOCAL -> GVisibility.Private
            Visibility.JAVA_PACKAGE -> GVisibility.Private
        }

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
                if (kotlinNativeFunctionNames.contains(simpleName.asString())) {
                    // Not sure if this is required here after all...
                    val returnType = typeParameters.last().getMermaidClassName()
                    val params = typeParameters.dropLast(1).joinToString { tp ->
                        tp.getMermaidClassName() + tp.bounds.cleanString() // TODO: clean
                    }
                    return "($params)->$returnType"
                } else {
                    return qualifiedName?.asString()?.removePrefix(packageName.asString() + ".")
                        ?: simpleName.asString()
                }
            }
        }
        return qualifiedName?.asString()?.removePrefix(packageName.asString() + ".") ?: simpleName.asString()
    }

    private fun KSClassDeclaration.getGClassType(): GClassType =
        when (classKind) {
            ClassKind.INTERFACE -> GClassType.Interface
            ClassKind.CLASS -> {
                when {
                    modifiers.contains(Modifier.SEALED) -> GClassType.SealedClass
                    modifiers.contains(Modifier.DATA) -> GClassType.DataClass
                    modifiers.contains(Modifier.VALUE) -> GClassType.ValueClass
                    else -> GClassType.Class
                }
            }

            ClassKind.ENUM_CLASS -> GClassType.Enum
            ClassKind.ENUM_ENTRY -> GClassType.EnumEntry
            ClassKind.OBJECT -> GClassType.Object
            ClassKind.ANNOTATION_CLASS -> GClassType.Annotation
        }
}