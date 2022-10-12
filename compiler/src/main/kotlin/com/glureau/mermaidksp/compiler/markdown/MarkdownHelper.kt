package com.glureau.mermaidksp.compiler.markdown

import com.glureau.mermaidksp.compiler.LocalType
import com.glureau.mermaidksp.compiler.kotlinNativeFunctionNames
import kotlin.math.max

fun StringBuilder.appendMdHeader(level: Int, header: String) = append("#".repeat(level) + " $header\n\n")
fun StringBuilder.appendMdH1(header: String) = appendMdHeader(1, header)
fun StringBuilder.appendMdH2(header: String) = appendMdHeader(2, header)
fun StringBuilder.appendMdH3(header: String) = appendMdHeader(3, header)
fun StringBuilder.appendMdH4(header: String) = appendMdHeader(4, header)
fun StringBuilder.appendMdH5(header: String) = appendMdHeader(5, header)
fun StringBuilder.appendMdH6(header: String) = appendMdHeader(6, header)
fun StringBuilder.appendMdMermaid(mermaidContent: String) = append(mdBlock(mermaidContent, "mermaid"))
fun StringBuilder.appendMdTable(headers: List<String>, vararg rows: List<String>) = append(mdTable(headers, *rows))

fun mdBlock(content: String, blockType: String = ""): String = """```$blockType
        |$content
        |```
        |
    """.trimMargin()

fun mdTable(headers: List<String>, vararg rows: List<String>): String {
    // Ensure the array is rectangular (no missing values
    rows.forEach { row ->
        require(headers.count() == row.count()) { "Missing cells, ${headers.count()} headers but ${row.count()} rows ($row)" }
    }

    val columnLengths = headers.mapIndexed { index, header ->
        var l = header.length
        rows.forEach {
            l = max(l, it[index].cleanStr().length)
        }
        l
    }

    return buildString {
        headers.forEachIndexed { index, header ->
            append("| ${header.cleanStr().padEnd(columnLengths[index] + 1)}")
        }
        append("|\n")

        columnLengths.forEach {
            append("|" + "-".repeat(it + 2))
        }
        append("|\n")

        rows.forEach { row ->
            row.forEachIndexed { index, cell ->
                append("| ${cell.cleanStr().padEnd(columnLengths[index] + 1)}")
            }
            append("|\n")
        }
        append("\n")
    }
}

fun String.cleanStr() = trim().removeSurrounding("\n").trim().replace("\n", "<div></div>")

fun LocalType.render(asProperty: Boolean = false): String =
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