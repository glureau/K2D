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

package com.glureau.mermaidksp.compiler

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview

@KotlinPoetKspPreview
class AggregatorClassVisitor : KSVisitorVoid() {
    val classes = mutableMapOf<String, GClass>()
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
        val klass = GClass(
            qualifiedName = qualifiedName,
            packageName = classDeclaration.packageName.asString(),
            originFile = classDeclaration.containingFile,
            visibility = classDeclaration.getMermaidVisibility(),
            symbolName = classDeclaration.getMermaidClassName(),
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
        klass.supers = classDeclaration.superTypes.mapNotNull { it.getMermaidClass() }
            // Because KSP consider now that every class extends Any, but we don't care about that in graphs.
            .filter { it.qualifiedName != "kotlin.Any" }
            .toList()
        klass.inners =
            classDeclaration.declarations.mapNotNull { if (it is KSClassDeclaration) scan(it) else null }.toList()

        classDeclaration.typeParameters.map {
            it.bounds.firstOrNull()?.getMermaidClass()
            val declaration: KSDeclaration? = it.bounds.firstOrNull()?.resolve()?.declaration
            val foo = if (declaration is KSClassDeclaration) {
                scan(declaration)
            } else null
            Logger.warn("Class: ${klass.symbolName} => $foo", it)
        }
        // TODO: Apply same compute on other "generics" computation
        // TODO: rework "symbolName" logic and use a specific mermaid method to display "~
        klass.generics = classDeclaration.typeParameters.map {
            Generics(
                it.name.asString(),
                it.bounds.first().getMermaidClass()!!
            )

        }
        //     fun KSTypeReference.getMermaidClass(): GClassOrBasic? {
        /*klass.generics = classDeclaration.typeParameters.map {
            scan(it.bounds.first().element)
        }*/
        return klass
    }

    private fun KSPropertyDeclaration.toMermaidProperty(classKind: ClassKind): GProperty? {
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
            ).apply {
                //TODO: generics = ...
            }
        } else {
            this.type.getMermaidClass()!!
        }

        val propName = this.simpleName.asString()
        val overrides =
            (classKind == ClassKind.ENUM_ENTRY) || this.modifiers.contains(Modifier.OVERRIDE) || this.findOverridee() != null
        return GProperty(
            visibility = this.getMermaidVisibility(),
            propName = propName,
            type = mermaidClass,
            overrides = overrides,
        )
    }

    // TODO: User option to be parameterized?
    private val ignoredFunctionNames = listOf("copy", "<init>") + (1..30).map { "component$it" }
    private fun KSFunctionDeclaration.toMermaidFunction(): GFunction? {
        if (this.simpleName.asString() in ignoredFunctionNames) return null
        return GFunction(
            visibility = this.getMermaidVisibility(),
            funcName = this.simpleName.asString(),
            // We could retrieve the name of parameters...
            parameters = this.parameters.mapNotNull { it.type.getMermaidClass() },
            returnType = this.returnType?.getMermaidClass(),
            overrides = this.modifiers.contains(Modifier.OVERRIDE)
        )
    }

    fun KSTypeReference.getMermaidClass(): GClassOrBasic? {
        val decl = resolve().declaration
        val qualifiedName = decl.qualifiedName?.asString() ?: return null
        return if (
            qualifiedName.startsWith("kotlin.") ||  // TODO: User option to be parameterized?
            qualifiedName.startsWith("java.") ||
            decl !is KSClassDeclaration
        ) {
            Basic(
                qualifiedName = qualifiedName,
                packageName = decl.packageName.asString(),
                symbolName = decl.simpleName.asString(),
            ).apply {
                //TODO: generics = ...
            }

            //Basic(decl.getMermaidClassName())
        } else {
            scan(decl)
        }
    }

    private fun KSDeclaration.getMermaidVisibility(): GVisibility =
        when (getVisibility()) {
            Visibility.PUBLIC -> GVisibility.Public
            Visibility.PRIVATE -> GVisibility.Private
            Visibility.PROTECTED -> GVisibility.Protected
            Visibility.INTERNAL -> GVisibility.Internal
            Visibility.LOCAL -> GVisibility.Private
            Visibility.JAVA_PACKAGE -> GVisibility.Private
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
                        tp.getMermaidClassName() + tp.bounds.cleanString() // TODO: clean
                    }
                    return "($params)->$returnType"
                } else {
                    // TODO: clean
                    return simpleName.asString() /*+ "~" + typeParameters.joinToString { tp ->
                        tp.getMermaidClassName() + tp.bounds.cleanString()
                    } + "~"*/
                }
            }
        }
        return simpleName.asString()
    }

    private fun KSClassDeclaration.getMermaidClassType(): GClassType =
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