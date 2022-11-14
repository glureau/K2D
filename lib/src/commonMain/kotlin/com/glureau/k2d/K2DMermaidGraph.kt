package com.glureau.k2d

@Repeatable
@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.SOURCE)
annotation class K2DMermaidGraph(
    val name: String,
    val symbolSelector: K2DSymbolSelectorAnnotation,
)