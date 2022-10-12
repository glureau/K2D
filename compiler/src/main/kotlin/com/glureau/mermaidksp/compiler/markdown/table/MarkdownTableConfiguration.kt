package com.glureau.mermaidksp.compiler.markdown.table

// TODO: make it accessible from annotations
data class MarkdownTableConfiguration(
    val markdownClassNameLevel: Int = 4,
    val showClassName: Boolean = true,
    val showClassDocumentation: Boolean = true,
    val showClassProperties: Boolean = true,
    val showClassFunctions: Boolean = true,
)