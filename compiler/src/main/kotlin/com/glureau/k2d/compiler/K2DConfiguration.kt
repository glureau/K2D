package com.glureau.k2d.compiler

import com.glureau.k2d.compiler.dokka.K2DDokkaConfig
import com.glureau.k2d.compiler.markdown.table.MarkdownClassTableConfiguration
import com.glureau.k2d.compiler.mermaid.MermaidRendererConfiguration
import kotlinx.serialization.Serializable

@Serializable
data class K2DConfiguration(
    var dokkaConfig: K2DDokkaConfig? = null,
    var customMermaidConfigs: List<K2DSymbolSelector> = emptyList(),
    val defaultMarkdownTableConfiguration: MarkdownClassTableConfiguration = MarkdownClassTableConfiguration(),
    val defaultMermaidConfiguration: MermaidRendererConfiguration = MermaidRendererConfiguration(),
)
