package com.glureau.k2d.compiler.mermaid

import com.glureau.k2d.compiler.GClassType
import com.glureau.k2d.compiler.GVisibility
import com.glureau.k2d.compiler.LocalType
import com.glureau.k2d.compiler.kotlinNativeFunctionNames

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


fun LocalType.renderForMermaid(asProperty: Boolean = false): String =
    if (kotlinNativeFunctionNames.contains(type.symbolName)) {
        val returnType = usedGenerics.last()
        // Special char, to look like parenthesis but avoid Mermaid limitation with lambda as a property type.
        // source: http://xahlee.info/comp/unicode_matching_brackets.html
        // Mermaid translates a line that contains standard parenthesis as a function, this is conflicting with Kotlin short syntax
        val parOpen = if (asProperty) "⟮" else "("
        val parClose = if (asProperty) "⟯" else ")"
        usedGenerics.dropLast(1)
            .joinToString(
                prefix = parOpen,
                postfix = "$parClose-> ${returnType.removePrefix("INVARIANT").trim()}"
            ) {
                it.removePrefix("INVARIANT").trim()
            }
    } else {
        val genericsStr =
            if (usedGenerics.isEmpty()) ""
            else usedGenerics.joinToString(
                prefix = "~",
                postfix = "~",
                transform = { it.removePrefix("INVARIANT").trim() }
            )
        type.symbolName + genericsStr
    }