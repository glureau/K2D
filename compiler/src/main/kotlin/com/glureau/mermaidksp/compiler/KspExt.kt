package com.glureau.mermaidksp.compiler

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import java.io.OutputStream
import kotlin.reflect.KProperty1

fun FileSpec.writeCode(environment: SymbolProcessorEnvironment, vararg sources: KSFile) {
    environment.codeGenerator.createNewFile(
        dependencies = Dependencies(aggregating = false, *sources),
        packageName = packageName,
        fileName = name
    ).use { outputStream ->
        outputStream.writer()
            .use {
                writeTo(it)
            }.also {
                Logger.warn("WROTE FILE $name")
            }
    }
}

fun OutputStream.appendText(str: String) {
    this.write(str.toByteArray())
}

fun TypeName.firstParameterizedType() = (this as ParameterizedTypeName).typeArguments.first()
fun TypeName.secondParameterizedType() = (this as ParameterizedTypeName).typeArguments[1]

@Suppress("UNCHECKED")
fun <T> KSAnnotation.getArg(kProp: KProperty1<*, *>) =
    arguments.firstOrNull { it.name?.asString() == kProp.name }?.value as T
