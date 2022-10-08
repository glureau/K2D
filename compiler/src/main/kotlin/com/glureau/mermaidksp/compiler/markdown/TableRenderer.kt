package com.glureau.mermaidksp.compiler.markdown

import com.glureau.mermaidksp.compiler.GClass

class TableRenderer {
    fun renderClassMembers(klass: GClass): String = buildString {
        klass.docString?.let { append(it + "\n\n") }

        if (klass.properties.isNotEmpty()) {
            appendMdH4("Properties\n")
            appendMdTable(
                headers = listOf("Name", "Type", "Comments"),
                *(klass.properties
                    .sortedBy { it.propName }
                    .map { listOf(it.propName, it.type.render(true), it.docString ?: "") }
                    .toTypedArray())
            )
        }

        val printableFunctions = klass.functions.filter { it.funcName !in listOf("equals", "hashCode", "toString") }
        if (printableFunctions.isNotEmpty()) {
            appendMdH4("Functions\n")
            appendMdTable(
                headers = listOf("Name", "Return Type", "Comments"),
                *(printableFunctions.sortedBy { it.funcName }
                    .map { listOf(it.funcName, it.returnType?.render() ?: "", it.docString ?: "") }
                    .toTypedArray())
            )
        }
    }
}