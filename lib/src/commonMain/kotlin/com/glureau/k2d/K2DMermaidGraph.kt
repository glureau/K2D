package com.glureau.k2d

@Repeatable
@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.SOURCE)
annotation class K2DMermaidGraph(
    val name: String,
    val symbolSelector: K2DSymbolSelectorAnnotation,
    val configuration: K2DMermaidRendererConfiguration = K2DMermaidRendererConfiguration(),
)

annotation class K2DMermaidRendererConfiguration(
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

    val baseUrl: String = ".",
)
