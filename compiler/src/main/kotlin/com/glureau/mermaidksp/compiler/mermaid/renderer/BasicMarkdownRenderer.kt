package com.glureau.mermaidksp.compiler.mermaid.renderer

import com.glureau.mermaidksp.compiler.GClass
import com.glureau.mermaidksp.compiler.writeMarkdown
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

class BasicMarkdownRenderer(private val environment: SymbolProcessorEnvironment) {
    fun render(data: MutableMap<String, GClass>, fileName: String) {
        val stringBuilder = StringBuilder()
        stringBuilder.append("# Mermaid & KSP\n")
        stringBuilder.append("```mermaid\n")
        stringBuilder.append(MermaidClassRenderer().renderClassDiagram(data))
        stringBuilder.append("```\n")

        data.keys.map { it.substringBeforeLast(".") }.distinct().forEach { packageName ->
            stringBuilder.append("## Package $packageName\n")
            stringBuilder.append("```mermaid\n")
            stringBuilder.append(MermaidClassRenderer().renderClassDiagram(data.filter { it.key.substringBeforeLast(".") == packageName }))
            stringBuilder.append("```\n")
        }

        data.forEach { (qualifiedName, klass) ->
            val implementedBy = data.filter { it.value.supers.any { s -> s == klass } }
            if (implementedBy.count() > 2) {
                stringBuilder.append("## Inheritance of $qualifiedName\n")
                stringBuilder.append("```mermaid\n")
                stringBuilder.append(
                    MermaidClassRenderer(
                        MermaidRendererConfiguration(
                            showHas = false
                        )
                    ).renderClassDiagram(implementedBy + (qualifiedName to klass))
                )
                stringBuilder.append("```\n")


                stringBuilder.append("## Inheritance of $qualifiedName reverse\n")
                stringBuilder.append("```mermaid\n")
                stringBuilder.append(
                    MermaidClassRenderer(
                        MermaidRendererConfiguration(
                            showHas = false,
                            basicIsDown = false,
                        )
                    ).renderClassDiagram(implementedBy + (qualifiedName to klass))
                )
                stringBuilder.append("```\n")
            }
        }

        val files = data.values.mapNotNull { it.originFile }

        val content = stringBuilder.toString().toByteArray()

        environment.logger.warn("Rendering markdown $fileName")
        environment.writeMarkdown(content, "", fileName, files)
    }
}
