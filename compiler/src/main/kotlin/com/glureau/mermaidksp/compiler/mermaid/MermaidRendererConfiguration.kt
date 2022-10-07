package com.glureau.mermaidksp.compiler.mermaid

data class MermaidRendererConfiguration(
    val showClassType: Boolean = true,

    val showPublic: Boolean = true,
    val showPrivate: Boolean = false,
    val showProtected: Boolean = false,
    val showInternal: Boolean = false,

    val showOverride: Boolean = false,

    val showImplements: Boolean = true,
    val showHas: Boolean = true,

    val basicIsDown: Boolean = false,

    val baseUrl: String = "."
)