package com.glureau.k2d.compiler.markdown.table

// TODO: make it accessible from annotations
data class MarkdownTableConfiguration(
    val markdownClassNameLevel: Int = 4,
    val showClassName: Boolean = true,
    val showClassDocumentation: Boolean = true,
    val showClassProperties: Boolean = true,
    val showClassPropertiesWithNoBackingField: Boolean = false,
    val showClassFunctions: Boolean = true,
)