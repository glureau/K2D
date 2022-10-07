package com.glureau.mermaidksp.compiler.dokka

import com.glureau.mermaidksp.compiler.GClass
import com.glureau.mermaidksp.compiler.writeMarkdown
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

class DokkaPackagesMarkdownRenderer(private val environment: SymbolProcessorEnvironment) {

    fun render(data: MutableMap<String, GClass>, moduleClasses: MutableSet<String>) {
        data.filter { moduleClasses.contains(it.key) }
            .values
            .map { it.packageName }
            .distinct()
            .forEach { packageName ->

                val classes = data.filter { it.value.packageName == packageName }

                val content = DokkaPackageMarkdownRenderer().render(data, packageName).toByteArray()
                val files = classes.values.mapNotNull { it.originFile }

                environment.logger.warn("Rendering package markdown $packageName")
                environment.writeMarkdown(content, packageName, "package", files)
            }
    }
}