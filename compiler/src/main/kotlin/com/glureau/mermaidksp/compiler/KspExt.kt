package com.glureau.mermaidksp.compiler

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSFile
import java.io.OutputStream
import kotlin.reflect.KProperty1

fun SymbolProcessorEnvironment.writeMarkdown(
    content: ByteArray,
    packageName: String = "",
    fileName: String,
    dependencies: List<KSFile>
) {
    try {
        codeGenerator.createNewFile(
            Dependencies(true, *dependencies.distinctBy { it.filePath }.toTypedArray()),
            packageName,
            fileName,
            "md"
        ).use {
            it.write(content)
            it.close()
        }
    } catch (faee: FileAlreadyExistsException) {
        // Not sure why yet, to be investigated
    }
}

fun OutputStream.appendText(str: String) {
    this.write(str.toByteArray())
}

@Suppress("UNCHECKED")
fun <T> KSAnnotation.getArg(kProp: KProperty1<*, *>) =
    arguments.firstOrNull { it.name?.asString() == kProp.name }?.value as T
