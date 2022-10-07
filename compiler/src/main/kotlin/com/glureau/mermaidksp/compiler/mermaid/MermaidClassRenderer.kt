package com.glureau.mermaidksp.compiler.mermaid

import com.glureau.mermaidksp.compiler.*

class MermaidClassRenderer(
    private val conf: MermaidRendererConfiguration = MermaidRendererConfiguration()
) {

    fun renderClassDiagram(classes: Map<String, GClass>): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("classDiagram\n")
        classes.values.forEach { c ->

            if (!conf.showInternal && c.visibility == GVisibility.Internal) return@forEach

            //if (c.classType == MermaidClassType.EnumEntry && c.properties.all { it.overrides } && c.functions.all { it.overrides }) return@forEach
            if (c.classType == GClassType.EnumEntry) return@forEach // Ignoring for now...

            val generics = c.generics.let {
                if (it.isEmpty()) "" else it.joinToString(
                    prefix = "~",
                    transform = { it.name + ":" + it.klassOrBasic.symbolName },
                    postfix = "~"
                )
            }
            val fullName = c.symbolName + generics
            stringBuilder.append("  class $fullName {\n")
            if (conf.showClassType) stringBuilder.append("    ${c.classType.asMermaid}\n")
            c.properties.forEach { p ->
                if (p.shouldDisplay(c))
                    stringBuilder.append("    ${p.visibility.asMermaid}${p.propName} ${p.type.symbolName}\n")
            }
            c.functions.forEach { f ->
                if (f.shouldDisplay())
                    stringBuilder.append("    ${f.visibility.asMermaid}${f.funcName}(${f.parameters.joinToString { it.symbolName }}) ${f.returnType?.symbolName ?: ""}\n")
            }
            if (c.classType == GClassType.Enum) {
                c.inners.forEach { i ->
                    stringBuilder.append("    [[ ${i.symbolName} ]]\n")
                }
            }
            stringBuilder.append("  }\n")
            if (conf.showImplements) {
                c.supers.forEach { s ->
                    if (conf.basicIsDown)
                        stringBuilder.append("  ${c.symbolName} ${Relationship.Implement} ${s.symbolName} : implements\n")
                    else
                        stringBuilder.append("  ${s.symbolName} ${Relationship.ImplementReverse} ${c.symbolName} : implements\n")
                }
            }
            if (conf.showHas) {
                c.properties.forEach { p ->
                    val shouldDisplay = p.shouldDisplay(c)
                    if (p.type is GClass && shouldDisplay) {
                        stringBuilder.append("  ${c.symbolName} ${Relationship.Composition} ${p.type.symbolName} : has\n")
                    }
                }
            }
            // Cannot write a tooltip for now when using generics: https://github.com/mermaid-js/mermaid/issues/2944
            stringBuilder.append("  click $fullName href \"${conf.baseUrl}/${c.packageName}/${c.symbolName.asDokkaHtmlPageFormat()}\"\n")// \"Open ${c.className.substringBefore("~")}\"\n")
        }
        return stringBuilder.toString()
    }

    private fun GProperty.shouldDisplay(containerClass: GClass): Boolean {
        if (!conf.showOverride && overrides) return false
        if (containerClass.classType == GClassType.Enum && propName in listOf("name", "ordinal")) return false
        return when (visibility) {
            GVisibility.Public -> conf.showPublic
            GVisibility.Private -> conf.showPrivate
            GVisibility.Protected -> conf.showProtected
            GVisibility.Internal -> conf.showInternal
        }
    }

    private fun GFunction.shouldDisplay(): Boolean =
        when (visibility) {
            GVisibility.Public -> conf.showPublic
            GVisibility.Private -> conf.showPrivate
            GVisibility.Protected -> conf.showProtected
            GVisibility.Internal -> conf.showInternal
        } &&
                (conf.showOverride || !overrides)
}

// TODO: To be injected via a LinkGenerator interface
private fun String.asDokkaHtmlPageFormat(): String {
    var str = this.substringBefore("~")
    while (str.contains(Regex("[A-Z]"))) {
        val index = str.indexOfFirst { it in 'A'..'Z' }
        str = str.substring(0, index) + "-" + str[index].lowercase() + str.substring(index + 1, str.length)
    }
    return "$str/index.html"
}

