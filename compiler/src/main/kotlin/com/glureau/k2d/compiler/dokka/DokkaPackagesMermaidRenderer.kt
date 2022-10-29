package com.glureau.k2d.compiler.dokka

import com.glureau.k2d.compiler.GClass
import com.glureau.k2d.compiler.writeMarkdown
import com.glureau.k2d.mermaid.K2DMermaidRendererConfiguration
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment

class DokkaPackagesMermaidRenderer(
    private val environment: SymbolProcessorEnvironment,
    private val configuration: K2DMermaidRendererConfiguration,
) {

    fun render(data: MutableMap<String, GClass>, moduleClasses: MutableSet<String>) {
        data.filter { moduleClasses.contains(it.key) }
            .values
            .map { it.packageName }
            .distinct()
            .forEach { packageName ->

                val classes = data.filter { it.value.packageName == packageName }

                val content = DokkaPackageMermaidRenderer(configuration).render(data, packageName).toByteArray()
                val files = classes.values.mapNotNull { it.originFile }

                environment.logger.warn("Rendering package markdown $packageName")
                environment.writeMarkdown(content, packageName, "package", files)
            }
    }
}