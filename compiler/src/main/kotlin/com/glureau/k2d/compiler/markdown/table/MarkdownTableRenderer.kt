package com.glureau.k2d.compiler.markdown.table

import com.glureau.k2d.compiler.GClass
import com.glureau.k2d.compiler.GClassType
import com.glureau.k2d.compiler.GFunction
import com.glureau.k2d.compiler.markdown.appendMdHeader
import com.glureau.k2d.compiler.markdown.appendMdTable
import com.glureau.k2d.compiler.markdown.renderForMarkdown

class MarkdownTableRenderer(
    private val config: K2DMarkdownTableConfiguration = K2DMarkdownTableConfiguration()
) {
    fun renderClassMembers(klass: GClass): String = buildString {
        if (klass.classType in listOf(GClassType.EnumEntry)) return@buildString
        if (!config.showCompanion && klass.simpleName == "Companion" && klass.classType == GClassType.Object) return@buildString
        if (!config.showInterface && klass.classType == GClassType.Interface) return@buildString

        val innerLevelCount = klass.symbolName.count { it == '.' }

        if (config.showClassName) {
            appendMdHeader(config.markdownClassNameLevel + innerLevelCount, klass.symbolName)
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
            if (config.showSectionName) {
                appendMdHeader(config.markdownClassNameLevel + 1 + innerLevelCount, "Properties\n")
            }
            appendMdTable(
                headers = listOf("Name", "Type", "Comments"),
                *(printableProperties
                    .map { listOf(it.propName, it.type.renderForMarkdown(), it.docString ?: "") }
                    .toTypedArray())
            )
        }

        val printableFunctions = klass.functions
            .filter { config.showClassFunctions }
            .filter { !it.hide }
            // methods from `kotlin.Any` are, 99.9% of the time not relevant
            .filter { it.funcName !in listOf("equals", "hashCode", "toString") }
            // Kotlin enums provide clone and compareTo methods by default, not really relevant for documentation
            .filter { klass.classType != GClassType.Enum || it.funcName !in listOf("clone", "compareTo") }
        if (printableFunctions.isNotEmpty()) {
            if (config.showSectionName) {
                appendMdHeader(config.markdownClassNameLevel + 1 + innerLevelCount, "Functions\n")
            }
            fun GFunction.displayName(): String = this.funcName + if (this.parameters.isEmpty()) "()" else "(..)"
            appendMdTable(
                headers = listOf("Name", "Return Type", "Comments"),
                *(printableFunctions.sortedBy { it.funcName }
                    .map { listOf(it.displayName(), it.returnType?.renderForMarkdown() ?: "", it.docString ?: "") }
                    .toTypedArray())
            )
        }

        if (klass.classType == GClassType.Enum) {
            appendMdTable(
                headers = listOf("Entry", "Comments"),
                *klass.inners.map { listOf(it.simpleName, it.docString ?: "") }.toTypedArray()
            )
        }
    }
}