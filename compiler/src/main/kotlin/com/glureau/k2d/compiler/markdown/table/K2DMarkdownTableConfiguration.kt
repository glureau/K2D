package com.glureau.k2d.compiler.markdown.table

import com.glureau.k2d.K2DClassMembersTableConfiguration
import kotlinx.serialization.Serializable

// TODO: make it accessible from annotations
@Serializable
data class K2DMarkdownTableConfiguration(
    var markdownClassNameLevel: Int = 4, // Because why not...
    var showClassName: Boolean = true,
    var showSectionName: Boolean = true, // Display "Properties" and "Functions" headers
    var showClassDocumentation: Boolean = true,
    var showClassProperties: Boolean = true,
    var showClassPropertiesWithNoBackingField: Boolean = true,
    var showClassFunctions: Boolean = true,
    var showCompanion: Boolean = true,
    var showInterface: Boolean = true,
    var showAbstract: Boolean = true,
    var showSealedClass: Boolean = true,
) {
    companion object {
        fun K2DClassMembersTableConfiguration.toDomain() =
            K2DMarkdownTableConfiguration(
                markdownClassNameLevel = markdownClassNameLevel,
                showClassName = showClassName,
                showSectionName = showSectionName,
                showClassDocumentation = showClassDocumentation,
                showClassProperties = showClassProperties,
                showClassPropertiesWithNoBackingField = showClassPropertiesWithNoBackingField,
                showClassFunctions = showClassFunctions,
                showCompanion = showCompanion,
                showInterface = showInterface,
                showAbstract = showAbstract
            )
    }
}