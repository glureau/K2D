package com.glureau.k2d.compiler.markdown

import com.glureau.k2d.compiler.GClass
import com.glureau.k2d.compiler.markdown.table.MarkdownTableRenderer
import com.glureau.k2d.compiler.mermaid.MermaidClassRenderer
import com.glureau.k2d.compiler.writeMarkdown
import com.glureau.k2d.compiler.mermaid.K2DMermaidRendererConfiguration
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

class DemoMarkdownRenderer(private val environment: SymbolProcessorEnvironment) {
    private val markdownTableRenderer = MarkdownTableRenderer()

    fun render(data: MutableMap<String, GClass>, fileName: String) {
        val stringBuilder = StringBuilder()
        stringBuilder.appendMdH1("Demo Mermaid & KSP")
        val confMermaid = K2DMermaidRendererConfiguration()
        stringBuilder.appendMdMermaid(MermaidClassRenderer(confMermaid).renderClassDiagram(data))

        data.keys.map { it.substringBeforeLast(".") }.distinct().forEach { packageName ->
            stringBuilder.appendMdH1("Package $packageName")
            stringBuilder.appendMdMermaid(
                MermaidClassRenderer(confMermaid)
                    .renderClassDiagram(data.filter { it.key.substringBeforeLast(".") == packageName })
            )
        }

        data.forEach { (qualifiedName, klass) ->
            val implementedBy = data.filter { it.value.supers.any { s -> s == klass } }
            if (implementedBy.count() > 2) {
                stringBuilder.appendMdH1("Inheritance of $qualifiedName")
                stringBuilder.appendMdMermaid(
                    MermaidClassRenderer(
                        K2DMermaidRendererConfiguration(
                            showHas = false
                        )
                    ).renderClassDiagram(implementedBy + (qualifiedName to klass))
                )
            }
        }

        data.values.forEach { klass ->
            if (klass.hide) return@forEach
            stringBuilder.appendMdH2("Details of ${klass.symbolName}")
            stringBuilder.append(markdownTableRenderer.renderClassMembers(klass))
        }

        val files = data.values.mapNotNull { it.originFile }

        val content = stringBuilder.toString().toByteArray()

        environment.logger.warn("Rendering markdown $fileName")
        environment.writeMarkdown(content, "", fileName, files)
    }
}
