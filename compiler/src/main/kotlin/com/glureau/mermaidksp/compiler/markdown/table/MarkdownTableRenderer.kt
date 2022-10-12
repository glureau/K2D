package com.glureau.mermaidksp.compiler.markdown.table

import com.glureau.mermaidksp.compiler.GClass
import com.glureau.mermaidksp.compiler.markdown.appendMdHeader
import com.glureau.mermaidksp.compiler.markdown.appendMdTable
import com.glureau.mermaidksp.compiler.markdown.render

class MarkdownTableRenderer(
    private val config: MarkdownTableConfiguration = MarkdownTableConfiguration()
) {
    fun renderClassMembers(klass: GClass): String = buildString {

        if (config.showClassName) {
            appendMdHeader(config.markdownClassNameLevel, klass.symbolName)
        }

        klass.docString?.let { append(it + "\n\n") }

        val printableProperties = klass.properties
            .filter { !it.hide }
        if (printableProperties.isNotEmpty()) {
            appendMdHeader(config.markdownClassNameLevel + 1, "Properties\n")
            appendMdTable(
                headers = listOf("Name", "Type", "Comments"),
                *(printableProperties
                    .sortedBy { it.propName }
                    .map { listOf(it.propName, it.type.render(true), it.docString ?: "") }
                    .toTypedArray())
            )
        }

        val printableFunctions = klass.functions
            .filter { it.funcName !in listOf("equals", "hashCode", "toString") }
            .filter { !it.hide }
        if (printableFunctions.isNotEmpty()) {
            appendMdHeader(config.markdownClassNameLevel + 1, "Functions\n")
            appendMdTable(
                headers = listOf("Name", "Return Type", "Comments"),
                *(printableFunctions.sortedBy { it.funcName }
                    .map { listOf(it.funcName, it.returnType?.render() ?: "", it.docString ?: "") }
                    .toTypedArray())
            )
        }
    }
}