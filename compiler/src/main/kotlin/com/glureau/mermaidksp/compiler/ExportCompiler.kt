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

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.isPublic
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview

// Trick to share the Logger everywhere without injecting the dependency everywhere
internal lateinit var sharedLogger: KSPLogger

internal object Logger : KSPLogger by sharedLogger

@KotlinPoetKspPreview
class ExportCompiler(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {

    init {
        sharedLogger = environment.logger
    }

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {

        Logger.warn("process")
        val exportVisitor = ExportVisitor(resolver)
        /*resolver.getSymbolsWithAnnotation(
            annotationName = KustomExport::class.qualifiedName!!,
            inDepth = true
        )
            .forEach { it.accept(exportVisitor, Unit) }
        */

        resolver.getAllFiles().forEach { it.accept(exportVisitor, Unit) }

        val content = exportVisitor.stringBuilder.toString() + "```"
        environment.codeGenerator.createNewFile(
            Dependencies(false, *exportVisitor.allFiles.toTypedArray()),
            "",
            "MermaidUml",
            "md"
        ).use {
            it.write(content.toByteArray())
            it.close()
        }

        return emptyList()
    }

    @KotlinPoetKspPreview
    inner class ExportVisitor(val resolver: Resolver) : KSVisitorVoid() {
        val stringBuilder = StringBuilder()
        val allFiles = mutableListOf<KSFile>()
        private val ignoredFunctionNames = listOf("copy", "<init>") + (1..30).map { "component$it" }

        init {
            stringBuilder.append("```mermaid\n")
            stringBuilder.append("classDiagram\n")
        }

        override fun visitFile(file: KSFile, data: Unit) {
            file.declarations.forEach { it.accept(this, Unit) }
            allFiles.add(file)
        }

        private fun KSClassDeclaration.typedName(): String {
            val classStr = when (classKind) {
                ClassKind.INTERFACE -> "I"
                ClassKind.CLASS -> "class"
                ClassKind.ENUM_CLASS -> "enum"
                ClassKind.ENUM_ENTRY -> "enum.entry"
                ClassKind.OBJECT -> "Single"
                ClassKind.ANNOTATION_CLASS -> "@"
            }
            val className = simpleName.asString()
            return className
            //return "`$classStr: $className`"
        }

        private fun KSDeclaration.typedName(): String {
            if (this is KSClassDeclaration) return typedName()
            TODO()
        }

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            stringBuilder.append("  class `${classDeclaration.typedName()}` {\n")
            classDeclaration.getAllProperties().forEach { prop ->
                val decl = prop.type.resolve().declaration
                if (prop.findOverridee() == null &&
                    prop.isPublic()
                ) {
                    stringBuilder.append("    ${decl.typedName()} ${prop.simpleName.asString()}\n")
                }
            }
            classDeclaration.getAllFunctions().forEach { func ->
                val funcName = func.simpleName.asString()
                if (func.findOverridee() == null &&
                    !ignoredFunctionNames.contains(funcName) &&
                    func.isPublic()
                ) {
                    stringBuilder.append("    $funcName(...)\n")
                }
            }
            stringBuilder.append("  }\n")


            classDeclaration.superTypes.forEach { sup ->
                val superName = sup.resolve().declaration.typedName()
                stringBuilder.append("  $superName <|-- ${classDeclaration.typedName()} : implements\n")
            }
            classDeclaration.getAllProperties().forEach { prop ->
                val decl = prop.type.resolve().declaration
                if (prop.findOverridee() == null &&
                    prop.isPublic() &&
                    !ignorePropertiesOfType(prop)
                ) {
                    stringBuilder.append("  ${decl.simpleName.asString()} *.. ${classDeclaration.typedName()} : has\n")
                }
            }
        }

        private fun ignorePropertiesOfType(prop: KSPropertyDeclaration): Boolean {
            val decl = prop.type.resolve().declaration
            val qualifiedName = decl.qualifiedName?.asString() ?: return true
            when {
                qualifiedName.startsWith("kotlin.") -> return true
            }
            return false
        }
    }
}

@KotlinPoetKspPreview
class ExportCompilerProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment) =
        ExportCompiler(environment)
}