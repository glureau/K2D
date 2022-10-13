package com.glureau.k2d.compiler.dokka

import com.glureau.k2d.compiler.GClass
import com.glureau.k2d.compiler.markdown.appendMdH1
import com.glureau.k2d.compiler.markdown.appendMdMermaid
import com.glureau.k2d.compiler.mermaid.MermaidClassRenderer
import com.glureau.k2d.compiler.writeMarkdown
import com.glureau.k2d.mermaid.K2DMermaidRendererConfiguration
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import java.io.File

class DokkaModuleMermaidRenderer(
    private val environment: SymbolProcessorEnvironment,
    private val configuration: K2DMermaidRendererConfiguration,
) {

    fun render(data: MutableMap<String, GClass>, moduleClasses: MutableSet<String>) {
        val classes = data.filter { moduleClasses.contains(it.key) }

        val firstOriginFile = classes.values.map { it.originFile }.firstOrNull { it != null } ?: return

        // Guess to find the module name...
        val packageAsPath = firstOriginFile.packageName.asString().replace(".", File.separator)
        val moduleName = firstOriginFile.filePath
            .substringBeforeLast(File.separator + packageAsPath + File.separator)
            // Hoping that such a trick could help support non-KMP project, to be tested...
            // Could be really great to have the module from KSP directly
            .substringBeforeLast(File.separator + "kotlin")
            .substringBeforeLast(File.separator + "commonMain")
            .substringBeforeLast(File.separator + "src")
            .substringAfterLast(File.separator)

        val stringBuilder = StringBuilder()
        stringBuilder.appendMdH1("Module $moduleName")
        val mermaidContent = MermaidClassRenderer(configuration)
            .renderClassDiagram(classes)
        stringBuilder.appendMdMermaid(mermaidContent)

        val content = stringBuilder.toString().toByteArray()

        val files = classes.values.mapNotNull { it.originFile }

        environment.logger.warn("Rendering module markdown $moduleName")
        environment.writeMarkdown(content, "", "module", files)
    }
}
