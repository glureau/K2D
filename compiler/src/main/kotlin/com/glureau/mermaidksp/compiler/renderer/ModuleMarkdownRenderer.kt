package com.glureau.mermaidksp.compiler.renderer

import com.glureau.mermaidksp.compiler.Logger
import com.glureau.mermaidksp.compiler.MermaidClass
import com.glureau.mermaidksp.compiler.writeMarkdown
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import java.io.File

class ModuleMarkdownRenderer(private val environment: SymbolProcessorEnvironment) {
    fun render(data: MutableMap<String, MermaidClass>, moduleClasses: MutableSet<String>) {
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
        stringBuilder.append("# Module $moduleName\n\n")
        stringBuilder.append("```mermaid\n")
        stringBuilder.append(
            MermaidClassRenderer(MermaidRendererConfiguration(baseUrl = "."))
                .renderClassDiagram(classes)
        )
        stringBuilder.append("```\n")
        val content = stringBuilder.toString().toByteArray()

        val files = classes.values.mapNotNull { it.originFile }

        environment.logger.warn("Rendering module markdown $moduleName")
        environment.writeMarkdown(content, "", "module", files)
    }
}
