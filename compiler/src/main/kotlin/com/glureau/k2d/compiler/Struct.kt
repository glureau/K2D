package com.glureau.k2d.compiler

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
    // val words = List<String>
    val type: LocalType,
    val isNullable: Boolean,
    val overrides: Boolean,
    val docString: String?,
    val hasBackingField: Boolean,
    val hide: Boolean,
)

data class LocalType(
    val type: GClassOrBasic,
    // at this location, the generics may have been substituted,
    // ex List<Any> is used locally as a List<String> (usedGenerics=["String"])
    val usedGenerics: List<String>,
)

// TODO: Handle nullable parameters
typealias GFunctionParameter = LocalType // No need for more... yet

data class GFunction(
    val visibility: GVisibility,
    val funcName: String,
    val parameters: List<GFunctionParameter>,
    val returnType: LocalType?,
    val overrides: Boolean,
    val docString: String?,
    val hide: Boolean,
)

data class Generics(
    val name: String,
    val klassOrBasic: GClassOrBasic,
)

sealed interface GClassOrBasic {
    val qualifiedName: String // Should be unique for a given class
    val packageName: String
    val symbolName: String
    val simpleName: String
    val generics: List<Generics>
}

data class Basic(
    override val qualifiedName: String,
    override val packageName: String,
    override val symbolName: String,
    override val simpleName: String,
    override var generics: List<Generics> = emptyList(),
) : GClassOrBasic

data class GClass constructor(
    override val qualifiedName: String,
    override val packageName: String,
    override val symbolName: String,
    override val simpleName: String,
    override var generics: List<Generics> = emptyList(),
    val originFile: KSFile?,
    val visibility: GVisibility,
    val classType: GClassType,
    val isAbstract: Boolean,
    val docString: String?,
    val hide: Boolean,
    var supers: List<GClassOrBasic> = emptyList(),
    var properties: List<GProperty> = emptyList(),
    var functions: List<GFunction> = emptyList(),
    var inners: List<GClass> = emptyList(),
) : GClassOrBasic