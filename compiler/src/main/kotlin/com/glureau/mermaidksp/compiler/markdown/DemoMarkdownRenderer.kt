package com.glureau.mermaidksp.compiler.markdown

import com.glureau.mermaidksp.compiler.GClass
import com.glureau.mermaidksp.compiler.mermaid.MermaidClassRenderer
import com.glureau.mermaidksp.compiler.mermaid.MermaidRendererConfiguration
import com.glureau.mermaidksp.compiler.writeMarkdown
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

class DemoMarkdownRenderer(private val environment: SymbolProcessorEnvironment) {
    private val tableRenderer = TableRenderer()

    fun render(data: MutableMap<String, GClass>, fileName: String) {
        val stringBuilder = StringBuilder()
        stringBuilder.appendMdH1("Demo Mermaid & KSP")
        stringBuilder.appendMdMermaid(MermaidClassRenderer().renderClassDiagram(data))

        data.keys.map { it.substringBeforeLast(".") }.distinct().forEach { packageName ->
            stringBuilder.appendMdH1("Package $packageName")
            stringBuilder.appendMdMermaid(
                MermaidClassRenderer()
                    .renderClassDiagram(data.filter { it.key.substringBeforeLast(".") == packageName })
            )
        }

        data.forEach { (qualifiedName, klass) ->
            val implementedBy = data.filter { it.value.supers.any { s -> s == klass } }
            if (implementedBy.count() > 2) {
                stringBuilder.appendMdH1("Inheritance of $qualifiedName")
                stringBuilder.appendMdMermaid(
                    MermaidClassRenderer(
                        MermaidRendererConfiguration(
                            showHas = false
                        )
                    ).renderClassDiagram(implementedBy + (qualifiedName to klass))
                )
            }
        }

        data.values.forEach { klass ->
            stringBuilder.appendMdH2("Details of ${klass.symbolName}")
            stringBuilder.append(tableRenderer.renderClassMembers(klass))
        }

        val files = data.values.mapNotNull { it.originFile }

        val content = stringBuilder.toString().toByteArray()

        environment.logger.warn("Rendering markdown $fileName")
        environment.writeMarkdown(content, "", fileName, files)
    }
}
