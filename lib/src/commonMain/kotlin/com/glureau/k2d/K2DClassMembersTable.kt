package com.glureau.k2d

@Repeatable
@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.SOURCE)
annotation class K2DClassMembersTable(
    val symbolSelector: K2DSymbolSelectorAnnotation,
)