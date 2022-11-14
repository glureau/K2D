package com.glureau.k2d.compiler

import com.glureau.k2d.NO_MATCH_REGEX
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSType
import kotlinx.serialization.Serializable
import com.glureau.k2d.K2DSymbolSelectorAnnotation as SSAnno

@Serializable
data class K2DSymbolSelector(
    val includesCurrentPackage: Boolean = true,
    val includesFqnRegex: String = NO_MATCH_REGEX,
    val includesClasses: List<String> = emptyList(), // full qualified names
    val includesClassesInheritingFrom: List<String> = emptyList(), // full qualified names

    val excludesCurrentPackage: Boolean = false,
    val excludesFqnRegex: String = NO_MATCH_REGEX,
    val excludesClasses: List<String> = emptyList(),
    val excludesClassesInheritingFrom: List<String> = emptyList(),
) {

    companion object {
        fun SSAnno.symbolSelector(
            // getAnnotationsByType doesn't provide a way to access user classes project for KClass
            // https://kotlinlang.slack.com/archives/C013BA8EQSE/p1643275195027000
            rawAnnotation: KSAnnotation,
            packageName: String?
        ): K2DSymbolSelector {

            val includesClasses = rawAnnotation.classesFrom(SSAnno::includesClasses)
            val includesClassesInheritingFrom = rawAnnotation.classesFrom(SSAnno::includesClassesInheritingFrom)

            val excludesClasses = rawAnnotation.classesFrom(SSAnno::excludesClasses)
            val excludesClassesInheritingFrom = rawAnnotation.classesFrom(SSAnno::excludesClassesInheritingFrom)

            fun KSType.qualifiedName() = declaration.qualifiedName?.asString()

            return K2DSymbolSelector(
                includesCurrentPackage = includesCurrentPackage,
                includesFqnRegex = includesFqnRegex,
                includesClasses = includesClasses.mapNotNull { it.qualifiedName() },
                includesClassesInheritingFrom = includesClassesInheritingFrom.mapNotNull { it.qualifiedName() },

                excludesCurrentPackage = excludesCurrentPackage,
                excludesFqnRegex = excludesFqnRegex,
                excludesClasses = excludesClasses.mapNotNull { it.qualifiedName() },
                excludesClassesInheritingFrom = excludesClassesInheritingFrom.mapNotNull { it.qualifiedName() },
            ).also {
                it.annotationPackageName = packageName
            }
        }
    }

    // Set by the compiler directly
    var annotationPackageName: String? = null

    fun matches(gClass: GClass): Boolean {
        val includes = isIncluded(gClass)
        if (!includes) return false

        val excludes = isExcluded(gClass)
        return !excludes
    }

    private fun isIncluded(gClass: GClass): Boolean {
        val packageName = annotationPackageName
        if (includesCurrentPackage && packageName != null && gClass.packageName.startsWith(packageName)) {
            return true
        }

        if (matchRegex(gClass, includesFqnRegex)) return true
        if (includesClasses.contains(gClass.qualifiedName)) return true
        if (isClassInheritsFrom(gClass, includesClassesInheritingFrom)) return true

        return false
    }

    private fun isExcluded(gClass: GClass): Boolean {
        val packageName = annotationPackageName
        if (excludesCurrentPackage && packageName != null && gClass.packageName.startsWith(packageName)) {
            return true
        }

        if (matchRegex(gClass, excludesFqnRegex)) return true
        if (excludesClasses.contains(gClass.qualifiedName)) return true
        if (isClassInheritsFrom(gClass, excludesClassesInheritingFrom)) return true

        return false
    }

    private fun matchRegex(gClass: GClass, fqnRegex: String): Boolean {
        if (fqnRegex == NO_MATCH_REGEX) return false
        return Regex(fqnRegex).matches(gClass.qualifiedName)
    }

    private fun isClassInheritsFrom(gclass: GClass, classesInheritingFrom: List<String>): Boolean {
        val supersQfn = gclass.supers.map { it.qualifiedName }
        return classesInheritingFrom.intersect(supersQfn).isNotEmpty()
    }
}
