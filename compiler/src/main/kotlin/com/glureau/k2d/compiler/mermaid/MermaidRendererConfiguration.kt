package com.glureau.k2d.compiler.mermaid

import com.glureau.k2d.K2DMermaidRendererConfiguration
import kotlinx.serialization.Serializable

@Serializable
data class MermaidRendererConfiguration(
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

    var baseUrl: String = ".",
) {
    companion object {
        fun K2DMermaidRendererConfiguration.toDomain() =
            MermaidRendererConfiguration(
                showClassType = showClassType,
                showPublic = showPublic,
                showPrivate = showPrivate,
                showProtected = showProtected,
                showInternal = showInternal,
                showCompanion = showCompanion,
                showOverride = showOverride,
                showClassPropertiesWithNoBackingField = showClassPropertiesWithNoBackingField,
                showImplements = showImplements,
                showHas = showHas,
                basicIsDown = basicIsDown,
                baseUrl = baseUrl,
            )
    }
}