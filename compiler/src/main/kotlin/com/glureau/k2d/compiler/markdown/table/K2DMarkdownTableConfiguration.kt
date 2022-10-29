package com.glureau.k2d.compiler.markdown.table

import kotlinx.serialization.Serializable

// TODO: make it accessible from annotations
@Serializable
data class K2DMarkdownTableConfiguration(
    var markdownClassNameLevel: Int = 4,
    var showSectionName: Boolean = false, // Display "Properties" and "Functions"
    var showClassName: Boolean = true,
    var showClassDocumentation: Boolean = true,
    var showClassProperties: Boolean = true,
    var showClassPropertiesWithNoBackingField: Boolean = true,
    var showClassFunctions: Boolean = true,
    var showCompanion: Boolean = false,
    var showInterface: Boolean = false,
)