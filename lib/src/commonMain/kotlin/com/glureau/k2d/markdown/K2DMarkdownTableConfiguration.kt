package com.glureau.k2d.markdown

import kotlinx.serialization.Serializable

// TODO: make it accessible from annotations
@Serializable
data class K2DMarkdownTableConfiguration(
    val markdownClassNameLevel: Int = 4,
    val showClassName: Boolean = true,
    val showClassDocumentation: Boolean = true,
    val showClassProperties: Boolean = true,
    val showClassPropertiesWithNoBackingField: Boolean = false,
    val showClassFunctions: Boolean = true,
)