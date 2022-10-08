package com.glureau.mermaidksp.compiler.mermaid

import com.glureau.mermaidksp.compiler.GClassType
import com.glureau.mermaidksp.compiler.GVisibility

val GVisibility.asMermaid: String
    get() = when (this) {
        GVisibility.Public -> "+"
        GVisibility.Private -> "-"
        GVisibility.Protected -> "#"
        GVisibility.Internal -> "~"
    }

val GClassType.asMermaid: String
    get() = when (this) {
        GClassType.Interface -> "<<interface>>"
        GClassType.Enum -> "<<enum>>"
        GClassType.EnumEntry -> "<<enum entry>>"
        GClassType.SealedClass -> "<<sealed>>"
        GClassType.DataClass -> "<<data class>>"
        GClassType.ValueClass -> "<<value class>>"
        GClassType.Class -> "<<class>>"
        GClassType.Object -> "<<object>>"
        GClassType.Annotation -> "<<annotation>>"
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
        MermaidRelationShip(MermaidRelationType.None, MermaidRelationLink.Dashed, MermaidRelationType.Composition)
    val Aggregation =
        MermaidRelationShip(MermaidRelationType.None, MermaidRelationLink.Solid, MermaidRelationType.Aggregation)
    // Do we need more? https://mermaid-js.github.io/mermaid/#/classDiagram?id=defining-relationship
}

