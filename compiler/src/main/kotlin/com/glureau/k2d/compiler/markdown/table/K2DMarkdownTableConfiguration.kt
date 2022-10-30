package com.glureau.k2d.compiler.markdown.table

import kotlinx.serialization.Serializable

// TODO: make it accessible from annotations
@Serializable
data class K2DMarkdownTableConfiguration(
    var markdownClassNameLevel: Int = 4, // Because why not...
    var showClassName: Boolean = true,
    var showSectionName: Boolean = true, // Display "Properties" and "Functions" headers
    var showClassDocumentation: Boolean = true,
    var showClassProperties: Boolean = true,
    var showClassPropertiesWithNoBackingField: Boolean = true,
    var showClassFunctions: Boolean = true,
    var showCompanion: Boolean = true,
    var showInterface: Boolean = true,
)