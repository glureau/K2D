package com.glureau.mermaidksp.compiler.renderer

import com.glureau.mermaidksp.compiler.MermaidClass
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

    val basicIsDown: Boolean = true
)

class MermaidClassRenderer(
    private val conf: MermaidRendererConfiguration = MermaidRendererConfiguration()
) {

    fun renderClassDiagram(classes: Map<String, MermaidClass>): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("classDiagram\n")
        classes.values.forEach { c ->
            stringBuilder.append("  class ${c.className} {\n")
            if (conf.showClassType) stringBuilder.append("    ${c.classType}\n")
            c.properties.forEach { p ->
                if (p.shouldDisplay())
                    stringBuilder.append("    ${p.visibility}${p.propName} ${p.type.className}\n")
            }
            c.functions.forEach { f ->
                if (f.shouldDisplay())
                    stringBuilder.append("${f.visibility}${f.funcName}(${f.parameters.joinToString { it.className }}) ${f.returnType?.className ?: ""}\n")
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
                    if (p.type is MermaidClass) {
                        stringBuilder.append("  ${c.className} ${Relationship.Composition} ${p.type.className} : has\n")
                    }
                }
            }
        }
        return stringBuilder.toString()
    }

    private fun MermaidProperty.shouldDisplay(): Boolean =
        when (visibility) {
            MermaidVisibility.Public -> conf.showPublic
            MermaidVisibility.Private -> conf.showPrivate
            MermaidVisibility.Protected -> conf.showProtected
            MermaidVisibility.Internal -> conf.showInternal
        } &&
            (conf.showOverride || !overrides)

    private fun MermaidFunction.shouldDisplay(): Boolean =
        when (visibility) {
            MermaidVisibility.Public -> conf.showPublic
            MermaidVisibility.Private -> conf.showPrivate
            MermaidVisibility.Protected -> conf.showProtected
            MermaidVisibility.Internal -> conf.showInternal
        } &&
            (conf.showOverride || !overrides)
}
