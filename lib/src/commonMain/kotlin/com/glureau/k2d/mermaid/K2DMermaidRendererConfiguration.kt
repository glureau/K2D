package com.glureau.k2d.mermaid

import kotlinx.serialization.Serializable

@Serializable
data class K2DMermaidRendererConfiguration(
    val showClassType: Boolean = true,

    val showPublic: Boolean = true,
    val showPrivate: Boolean = false,
    val showProtected: Boolean = false,
    val showInternal: Boolean = false,
    val showCompanion: Boolean = true,

    val showOverride: Boolean = false,
    val showClassPropertiesWithNoBackingField: Boolean = true,

    val showImplements: Boolean = true,
    val showHas: Boolean = true,

    val basicIsDown: Boolean = false,

    val baseUrl: String = "."
)