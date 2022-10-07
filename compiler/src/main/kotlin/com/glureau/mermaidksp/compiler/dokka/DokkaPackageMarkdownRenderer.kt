package com.glureau.mermaidksp.compiler.dokka

import com.glureau.mermaidksp.compiler.GClass
import com.glureau.mermaidksp.compiler.markdown.appendMdH1
import com.glureau.mermaidksp.compiler.markdown.appendMdMermaid
import com.glureau.mermaidksp.compiler.mermaid.renderer.MermaidClassRenderer
import com.glureau.mermaidksp.compiler.mermaid.renderer.MermaidRendererConfiguration

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