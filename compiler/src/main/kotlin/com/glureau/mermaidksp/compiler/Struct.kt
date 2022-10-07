package com.glureau.mermaidksp.compiler

import com.google.devtools.ksp.symbol.KSFile

enum class GVisibility {
    Public,
    Private,
    Protected,
    Internal,
}

enum class GClassType {
    Interface,
    Enum,
    EnumEntry,
    SealedClass,
    DataClass,
    ValueClass,
    Class,
    Object,
    Annotation,
}

data class GProperty(
    val visibility: GVisibility,
    val propName: String,
    val type: GClassOrBasic,
    val overrides: Boolean
)

data class GFunction(
    val visibility: GVisibility,
    val funcName: String,
    val parameters: List<GClassOrBasic>,
    val returnType: GClassOrBasic?,
    val overrides: Boolean
)

data class Generics(
    val name: String,
    val klassOrBasic: GClassOrBasic,
)

sealed interface GClassOrBasic {
    val qualifiedName: String // Should be unique for a given class
    val packageName: String
    val symbolName: String
    val generics: List<Generics>
}

data class Basic(
    override val qualifiedName: String,
    override val packageName: String,
    override val symbolName: String,
    override var generics: List<Generics> = emptyList(),
) : GClassOrBasic

data class GClass constructor(
    override val qualifiedName: String,
    override val packageName: String,
    override val symbolName: String,
    override var generics: List<Generics> = emptyList(),
    val originFile: KSFile?,
    val visibility: GVisibility,
    val classType: GClassType,
    var supers: List<GClassOrBasic> = emptyList(),
    var properties: List<GProperty> = emptyList(),
    var functions: List<GFunction> = emptyList(),
    var inners: List<GClass> = emptyList(),
) : GClassOrBasic