package com.glureau.k2d.compiler

import com.glureau.k2d.compiler.dokka.K2DDokkaConfig
import com.glureau.k2d.compiler.markdown.table.K2DMarkdownTableConfiguration
import com.glureau.k2d.compiler.mermaid.K2DMermaidRendererConfiguration
import kotlinx.serialization.Serializable

@Serializable
data class K2DConfiguration(
    var dokkaConfig: K2DDokkaConfig? = null,
    var customMermaidConfigs: List<K2DSymbolSelector> = emptyList(),
    val defaultMarkdownTableConfiguration: K2DMarkdownTableConfiguration = K2DMarkdownTableConfiguration(),
    val defaultMermaidConfiguration: K2DMermaidRendererConfiguration = K2DMermaidRendererConfiguration(),
)
