package com.glureau.k2d.compiler.dokka

import com.glureau.k2d.compiler.GClass
import com.glureau.k2d.compiler.markdown.appendMdH1
import com.glureau.k2d.compiler.markdown.appendMdMermaid
import com.glureau.k2d.compiler.mermaid.MermaidClassRenderer
import com.glureau.k2d.compiler.mermaid.K2DMermaidRendererConfiguration

class DokkaPackageMermaidRenderer(
    private val configuration: K2DMermaidRendererConfiguration,
) {
    fun render(data: MutableMap<String, GClass>, packageName: String): String {
        val classes = data.filter { it.value.packageName == packageName }

        val stringBuilder = StringBuilder()
        stringBuilder.appendMdH1("Package $packageName")
        val mermaidContent = MermaidClassRenderer(configuration).renderClassDiagram(classes)
        stringBuilder.appendMdMermaid(mermaidContent)

        return stringBuilder.toString()
    }
}