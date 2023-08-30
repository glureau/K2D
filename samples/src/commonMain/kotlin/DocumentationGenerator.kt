@file:K2DMermaidGraph(
    name = "Shapes23",
    symbolSelector = K2DSymbolSelectorAnnotation(
        includesCurrentPackage = true,
        excludesClasses = [Position::class],
        excludesFqnRegex = "sample\\.[a-z]+.*", // only in sample package, given the convention package name
    )
)

@file:K2DClassMembersTable(
    symbolSelector = K2DSymbolSelectorAnnotation(
        includesCurrentPackage = true
    )
)

@file:K2DClassMembersTable(
    name = "Circle_no_functions",
    symbolSelector = K2DSymbolSelectorAnnotation(
        includesCurrentPackage = false,
        includesClasses = [Circle::class],
    ),
    configuration = K2DClassMembersTableConfiguration(
        showClassFunctions = false,
    )
)

package sample

import com.glureau.k2d.K2DClassMembersTable
import com.glureau.k2d.K2DClassMembersTableConfiguration
import com.glureau.k2d.K2DMermaidGraph
import com.glureau.k2d.K2DSymbolSelectorAnnotation
