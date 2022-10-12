package com.glureau.k2d

import kotlinx.serialization.Serializable

@Serializable
data class K2DConfiguration(
    val dokkaConfig: K2DDokkaConfig? = null,
    val customMermaidConfigs: List<K2DSymbolSelector> = emptyList()
)