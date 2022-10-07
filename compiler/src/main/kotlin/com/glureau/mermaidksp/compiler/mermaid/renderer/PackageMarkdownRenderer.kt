package com.glureau.mermaidksp.compiler.mermaid.renderer

import com.glureau.mermaidksp.compiler.GClass
import com.glureau.mermaidksp.compiler.writeMarkdown
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

class PackageMarkdownRenderer(private val environment: SymbolProcessorEnvironment) {
    fun render(data: MutableMap<String, GClass>, moduleClasses: MutableSet<String>) {
        data.filter { moduleClasses.contains(it.key) }
            .values
            .map { it.packageName }
            .distinct()
            .forEach { packageName ->

                val classes = data.filter { it.value.packageName == packageName }

                val stringBuilder = StringBuilder()
                stringBuilder.append("# Package $packageName\n\n")
                stringBuilder.append("```mermaid\n")
                stringBuilder.append(MermaidClassRenderer(MermaidRendererConfiguration(baseUrl = "..")).renderClassDiagram(classes))
                stringBuilder.append("```\n")
                val content = stringBuilder.toString().toByteArray()

                val files = classes.values.mapNotNull { it.originFile }

                environment.logger.warn("Rendering package markdown $packageName")
                environment.writeMarkdown(content, packageName, "package", files)
            }
    }
}