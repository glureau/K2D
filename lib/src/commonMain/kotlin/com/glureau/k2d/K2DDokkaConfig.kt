package com.glureau.k2d

import kotlinx.serialization.Serializable

@Serializable
data class K2DDokkaConfig(
    var generateMermaidOnModules: Boolean = true,
    var generateMermaidOnPackages: Boolean = true,
)