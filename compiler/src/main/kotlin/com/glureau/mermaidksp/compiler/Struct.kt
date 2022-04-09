package com.glureau.mermaidksp.compiler

import com.google.devtools.ksp.symbol.KSFile

enum class MermaidVisibility(private val txt: String) {
    Public("+"),
    Private("-"),
    Protected("#"),
    Internal("~");

    override fun toString() = txt
}

enum class MermaidRelationType(private val txt: String) {
    Inheritance("<|"),
    Composition("*"),
    Aggregation("o"),
    AssociationRight(">"),
    AssociationLeft("<"),
    Realization("|>"),
    None("");

    override fun toString() = txt
}

enum class MermaidRelationLink(private val txt: String) {
    Solid("--"),
    Dashed("..");

    override fun toString() = txt
}

data class MermaidRelationShip(
    val left: MermaidRelationType,
    val link: MermaidRelationLink,
    val right: MermaidRelationType,
) {
    override fun toString() = "$left$link$right"
}

object Relationship {
    val Implement =
        MermaidRelationShip(MermaidRelationType.None, MermaidRelationLink.Solid, MermaidRelationType.Realization)
    val ImplementReverse =
        MermaidRelationShip(MermaidRelationType.Inheritance, MermaidRelationLink.Solid, MermaidRelationType.None)
    val Composition =
        MermaidRelationShip(MermaidRelationType.None, MermaidRelationLink.Solid, MermaidRelationType.Composition)
    val Aggregation =
        MermaidRelationShip(MermaidRelationType.None, MermaidRelationLink.Solid, MermaidRelationType.Aggregation)
    // Do we need more? https://mermaid-js.github.io/mermaid/#/classDiagram?id=defining-relationship
}

enum class MermaidClassType(private val txt: String) {
    Interface("<<interface>>"),
    Enum("<<enum>>"),
    EnumEntry("<<enum entry>>"),
    Sealed("<<sealed>>"),
    Class("<<class>>"),
    Object("<<object>>"),
    Annotation("<<annotation>>"),
    ;

    override fun toString() = txt
}

data class MermaidProperty(
    val visibility: MermaidVisibility,
    val propName: String,
    val type: MermaidClassOrBasic,
    val overrides: Boolean
)

data class MermaidFunction(
    val visibility: MermaidVisibility,
    val funcName: String,
    val parameters: List<MermaidClassOrBasic>,
    val returnType: MermaidClassOrBasic?,
    val overrides: Boolean
)

sealed interface MermaidClassOrBasic {
    val className: String
}

data class Basic(override val className: String) : MermaidClassOrBasic
data class MermaidClass(
    val qualifiedName: String, // Should be unique for a given class
    val originFile: KSFile?,
    val visibility: MermaidVisibility,
    override val className: String,
    val classType: MermaidClassType,
    var supers: List<MermaidClassOrBasic> = emptyList(),
    var properties: List<MermaidProperty> = emptyList(),
    var functions: List<MermaidFunction> = emptyList(),
) : MermaidClassOrBasic