package com.glureau.k2d

import kotlin.reflect.KClass

annotation class K2DSymbolSelectorAnnotation(
    val includesFqnRegex: String = ".*", // field is used as a Regex
    val includesClasses: Array<KClass<*>> = emptyArray(),
    val includesClassesInheritingFrom: Array<KClass<*>> = emptyArray(),

    val excludesFqnRegex: String = ".*", // field is used as a Regex
    val excludesClasses: Array<KClass<*>> = emptyArray(),
    val excludesClassesInheritingFrom: Array<KClass<*>> = emptyArray(),
)