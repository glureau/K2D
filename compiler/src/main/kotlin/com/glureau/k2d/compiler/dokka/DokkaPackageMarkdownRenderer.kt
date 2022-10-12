package com.glureau.k2d.compiler.dokka

import com.glureau.k2d.compiler.GClass
import com.glureau.k2d.compiler.markdown.appendMdH1
import com.glureau.k2d.compiler.markdown.appendMdMermaid
import com.glureau.k2d.compiler.mermaid.MermaidClassRenderer
import com.glureau.k2d.compiler.mermaid.MermaidRendererConfiguration

class DokkaPackageMarkdownRenderer {
    fun render(data: MutableMap<String, GClass>, packageName: String): String {
        val classes = data.filter { it.value.packageName == packageName }

        val stringBuilder = StringBuilder()
        stringBuilder.appendMdH1("Package $packageName")
        val mermaidContent =
            MermaidClassRenderer(MermaidRendererConfiguration(baseUrl = "..")).renderClassDiagram(classes)
        stringBuilder.appendMdMermaid(mermaidContent)

        return stringBuilder.toString()
    }
}