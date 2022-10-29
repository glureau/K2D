package com.glureau.k2d.compiler.mermaid

import kotlinx.serialization.Serializable

@Serializable
data class K2DMermaidRendererConfiguration(
    var showClassType: Boolean = true,

    var showPublic: Boolean = true,
    var showPrivate: Boolean = false,
    var showProtected: Boolean = false,
    var showInternal: Boolean = false,
    var showCompanion: Boolean = true,

    var showOverride: Boolean = false,
    var showClassPropertiesWithNoBackingField: Boolean = true,

    var showImplements: Boolean = true,
    var showHas: Boolean = true,

    var basicIsDown: Boolean = false,

    var baseUrl: String = "."
)