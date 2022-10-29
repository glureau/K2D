package com.glureau.k2d.compiler

import kotlinx.serialization.Serializable

@Serializable
data class K2DSymbolSelector(
    val includesFqnRegex: String = ".*", // field is used as a Regex
    val excludesFqnRegex: String = ".*", // field is used as a Regex
)