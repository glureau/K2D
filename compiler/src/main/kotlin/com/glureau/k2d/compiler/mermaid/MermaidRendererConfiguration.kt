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
                // If no value passed as parameters, K2 requires to re-define the default value...
                showClassType = runCatching { showClassType }.getOrElse { true },
                showPublic = runCatching { showPublic }.getOrElse { true },
                showPrivate = runCatching { showPrivate }.getOrElse { false },
                showProtected = runCatching { showProtected }.getOrElse { false },
                showInternal = runCatching { showInternal }.getOrElse { false },
                showCompanion = runCatching { showCompanion }.getOrElse { true },
                showOverride = runCatching { showOverride }.getOrElse { false },
                showClassPropertiesWithNoBackingField = runCatching { showClassPropertiesWithNoBackingField }.getOrElse { true },
                showImplements = runCatching { showImplements }.getOrElse { true },
                showHas = runCatching { showHas }.getOrElse { true },
                basicIsDown = runCatching { basicIsDown }.getOrElse { false },
                baseUrl = runCatching { baseUrl }.getOrElse { "." },
            )
    }
}