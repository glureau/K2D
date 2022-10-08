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

            stringBuilder.append("  class ${c.fullTypeName()} {\n")
            if (conf.showClassType) stringBuilder.append("    ${c.classType.asMermaid}\n")
            c.properties.forEach { p ->
                if (p.shouldDisplay(c))
                    stringBuilder.append("    ${p.visibility.asMermaid}${p.propName} ${p.type.render(true)}\n")

                //stringBuilder.append(p.type.toString() + "\n") // TODO: remove this line, debug only
            }

            c.functions.forEach { f ->
                if (f.shouldDisplay()) {
                    // TODO: method to render lambdas can be shared (somewhere else)
                    val paramsString = f.parameters.joinToString { it.render() }
                    //val paramsString = f.parameters.joinToString { it.usedGenerics.joinToString("|") }
                    var returnString = f.returnType?.render() ?: ""
                    if (returnString == "Unit") returnString = "" // Ignore Unit
                    stringBuilder.append("    ${f.visibility.asMermaid}${f.funcName}($paramsString) ${returnString}\n")
                }
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
                        stringBuilder.append("  ${c.fullTypeName()} ${Relationship.Implement} ${s.fullTypeName()} : implements\n")
                    else
                        stringBuilder.append("  ${s.fullTypeName()} ${Relationship.ImplementReverse} ${c.fullTypeName()} : implements\n")
                }
            }
            if (conf.showHas) {
                c.properties.forEach { p ->
                    val shouldDisplay = p.shouldDisplay(c)
                    if (p.type.type is GClass && shouldDisplay) {
                        stringBuilder.append("  ${c.fullTypeName()} ${Relationship.Composition} ${p.type.render(true)} : has\n")
                    }
                }
            }
            // Cannot write a tooltip for now when using generics: https://github.com/mermaid-js/mermaid/issues/2944
            stringBuilder.append("  click ${c.fullTypeName()} href \"${conf.baseUrl}/${c.packageName}/${c.symbolName.asDokkaHtmlPageFormat()}\"\n")// \"Open ${c.className.substringBefore("~")}\"\n")
        }
        return stringBuilder.toString()
    }

    fun GClassOrBasic.fullTypeName(): String {
        val genericsStr =
            if (generics.isEmpty()) ""
            else generics.joinToString(
                prefix = "~",
                transform = { it.name + ":" + it.klassOrBasic.symbolName },
                postfix = "~"
            )
        return symbolName + genericsStr
    }

    private fun LocalType.render(asProperty: Boolean = false): String =
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

