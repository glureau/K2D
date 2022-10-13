package com.glureau.k2d

import com.glureau.k2d.markdown.K2DMarkdownTableConfiguration
import com.glureau.k2d.mermaid.K2DMermaidRendererConfiguration
import kotlinx.serialization.Serializable

@Serializable
data class K2DConfiguration(
    var dokkaConfig: K2DDokkaConfig? = null,
    var customMermaidConfigs: List<K2DSymbolSelector> = emptyList(),
    val defaultMarkdownTableConfiguration: K2DMarkdownTableConfiguration = K2DMarkdownTableConfiguration(),
    val defaultMermaidConfiguration: K2DMermaidRendererConfiguration = K2DMermaidRendererConfiguration(),
)
