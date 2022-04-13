package com.glureau.mermaidksp.compiler

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSFile
import java.io.OutputStream

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
