package com.glureau.k2d

import kotlin.reflect.KClass

// By default, we include all classes that starts with the same package that the file containing the annotation
annotation class K2DSymbolSelectorAnnotation(
    val includesCurrentPackage: Boolean = true,
    val includesFqnRegex: String = NO_MATCH_REGEX, // field is used as a Regex
    val includesClasses: Array<KClass<*>> = [],
    val includesClassesInheritingFrom: Array<KClass<*>> = [],

    val excludesCurrentPackage: Boolean = false,
    val excludesFqnRegex: String = NO_MATCH_REGEX, // field is used as a Regex
    val excludesClasses: Array<KClass<*>> = [],
    val excludesClassesInheritingFrom: Array<KClass<*>> = [],
)
