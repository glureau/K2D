package com.glureau.k2d

import kotlinx.serialization.Serializable

@Serializable
data class K2DDokkaConfig(
    val generateMermaidOnModules: Boolean = true,
    val generateMermaidOnPackages: Boolean = true,
)