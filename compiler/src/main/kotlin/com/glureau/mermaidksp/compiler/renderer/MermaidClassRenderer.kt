package com.glureau.mermaidksp.compiler.renderer

import com.glureau.mermaidksp.compiler.Logger
import com.glureau.mermaidksp.compiler.MermaidClass
import com.glureau.mermaidksp.compiler.MermaidClassType
import com.glureau.mermaidksp.compiler.MermaidFunction
import com.glureau.mermaidksp.compiler.MermaidProperty
import com.glureau.mermaidksp.compiler.MermaidVisibility
import com.glureau.mermaidksp.compiler.Relationship

data class MermaidRendererConfiguration(
    val showClassType: Boolean = true,

    val showPublic: Boolean = true,
    val showPrivate: Boolean = false,
    val showProtected: Boolean = false,
    val showInternal: Boolean = false,

    val showOverride: Boolean = false,

    val showImplements: Boolean = true,
    val showHas: Boolean = true,

    val basicIsDown: Boolean = false,

    val baseUrl: String = "."
)

class MermaidClassRenderer(
    private val conf: MermaidRendererConfiguration = MermaidRendererConfiguration()
) {

    fun renderClassDiagram(classes: Map<String, MermaidClass>): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("classDiagram\n")
        classes.values.forEach { c ->

            if (!conf.showInternal && c.visibility == MermaidVisibility.Internal) return@forEach

            //if (c.classType == MermaidClassType.EnumEntry && c.properties.all { it.overrides } && c.functions.all { it.overrides }) return@forEach
            if (c.classType == MermaidClassType.EnumEntry) return@forEach // Ignoring for now...

            stringBuilder.append("  class ${c.className} {\n")
            if (conf.showClassType) stringBuilder.append("    ${c.classType}\n")
            c.properties.forEach { p ->
                if (p.shouldDisplay(c))
                    stringBuilder.append("    ${p.visibility}${p.propName} ${p.type.className}\n")
            }
            c.functions.forEach { f ->
                if (f.shouldDisplay())
                    stringBuilder.append("    ${f.visibility}${f.funcName}(${f.parameters.joinToString { it.className }}) ${f.returnType?.className ?: ""}\n")
            }
            if (c.classType == MermaidClassType.Enum) {
                c.inners.forEach { i ->
                    stringBuilder.append("    [[ ${i.className} ]]\n")
                }
            }
            stringBuilder.append("  }\n")
            if (conf.showImplements) {
                c.supers.forEach { s ->
                    if (conf.basicIsDown)
                        stringBuilder.append("  ${c.className} ${Relationship.Implement} ${s.className} : implements\n")
                    else
                        stringBuilder.append("  ${s.className} ${Relationship.ImplementReverse} ${c.className} : implements\n")
                }
            }
            if (conf.showHas) {
                c.properties.forEach { p ->
                    val shouldDisplay = p.shouldDisplay(c)
                    Logger.warn("Should display? ${c.className}->${p.type.className} ${p.type is MermaidClass} && $shouldDisplay")
                    if (p.type is MermaidClass && shouldDisplay) {
                        stringBuilder.append("  ${c.className} ${Relationship.Composition} ${p.type.className} : has\n")
                    }
                }
            }
            // Cannot write a tooltip for now when using generics: https://github.com/mermaid-js/mermaid/issues/2944
            stringBuilder.append("  click ${c.className} href \"${conf.baseUrl}/${c.packageName}/${c.className.asDokkaHtmlPageFormat()}\"\n")// \"Open ${c.className.substringBefore("~")}\"\n")
        }
        return stringBuilder.toString()
    }

    private fun MermaidProperty.shouldDisplay(containerClass: MermaidClass): Boolean {
        Logger.warn(
            "Class=${containerClass.className}\n" +
                "conf.showOverride=${conf.showOverride}\n" +
                "overrides=$overrides\n" +
                "propName=$propName\n" +
                "visibility=$visibility\n" +
                "conf.showPublic=${conf.showPublic}"
        )
        if (!conf.showOverride && overrides) return false.also {
            Logger.warn("OVERRIDE IS OUT")
        }
        if (containerClass.classType == MermaidClassType.Enum && propName in listOf("name", "ordinal")) return false.also {
            Logger.warn("ENUM IS OUT")
        }
        return when (visibility) {
            MermaidVisibility.Public -> conf.showPublic
            MermaidVisibility.Private -> conf.showPrivate
            MermaidVisibility.Protected -> conf.showProtected
            MermaidVisibility.Internal -> conf.showInternal
        }
    }

    private fun MermaidFunction.shouldDisplay(): Boolean =
        when (visibility) {
            MermaidVisibility.Public -> conf.showPublic
            MermaidVisibility.Private -> conf.showPrivate
            MermaidVisibility.Protected -> conf.showProtected
            MermaidVisibility.Internal -> conf.showInternal
        } &&
            (conf.showOverride || !overrides)
}

private fun String.asDokkaHtmlPageFormat(): String {
    var str = this.substringBefore("~")
    while (str.contains(Regex("[A-Z]"))) {
        val index = str.indexOfFirst { it in 'A'..'Z' }
        str = str.substring(0, index) + "-" + str[index].lowercase() + str.substring(index + 1, str.length)
    }
    return "$str/index.html"
}

