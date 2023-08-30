package com.glureau.k2d

@Repeatable
@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.SOURCE)
annotation class K2DClassMembersTable(
    val name: String = "",
    val symbolSelector: K2DSymbolSelectorAnnotation,
    val configuration: K2DClassMembersTableConfiguration = K2DClassMembersTableConfiguration(),
)


annotation class K2DClassMembersTableConfiguration(
    val markdownClassNameLevel: Int = 4, // Because why not...
    val showClassName: Boolean = true,
    val showSectionName: Boolean = true, // Display "Properties" and "Functions" headers
    val showClassDocumentation: Boolean = true,
    val showClassProperties: Boolean = true,
    val showClassPropertiesWithNoBackingField: Boolean = true,
    val showClassFunctions: Boolean = true,
    val showCompanion: Boolean = true,
    val showInterface: Boolean = true,
    val showAbstract: Boolean = true,
    val showSealedClass: Boolean = true,
)