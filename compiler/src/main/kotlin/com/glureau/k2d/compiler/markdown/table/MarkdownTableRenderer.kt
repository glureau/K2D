package com.glureau.k2d.compiler.markdown.table

import com.glureau.k2d.compiler.GClass
import com.glureau.k2d.compiler.GClassType
import com.glureau.k2d.compiler.markdown.appendMdHeader
import com.glureau.k2d.compiler.markdown.appendMdTable
import com.glureau.k2d.compiler.markdown.render

class MarkdownTableRenderer(
    private val config: MarkdownTableConfiguration = MarkdownTableConfiguration()
) {
    fun renderClassMembers(klass: GClass): String = buildString {

        if (config.showClassName) {
            appendMdHeader(config.markdownClassNameLevel, klass.symbolName)
        }

        if (config.showClassDocumentation) {
            klass.docString?.let { append(it + "\n\n") }
        }

        val printableProperties = klass.properties
            .filter { config.showClassProperties }
            .filter { !it.hide }
            .filter {
                // Ignore backingField if the class type is abstract by default
                klass.classType in listOf(GClassType.Interface, GClassType.ValueClass) ||
                        it.hasBackingField ||
                        config.showClassPropertiesWithNoBackingField
            }
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
            .filter { config.showClassFunctions }
            .filter { !it.hide }
            // methods from `kotlin.Any` are, 99.9% of the time not relevant
            .filter { it.funcName !in listOf("equals", "hashCode", "toString") }
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