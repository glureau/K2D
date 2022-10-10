package foo

import ClassMembersTable
import K2DSymbolSelectorAnnotation
import MermaidGraph

@MermaidGraph(
    "Shapes23",
    symbolSelector = K2DSymbolSelectorAnnotation(
        includesFqnRegex = "sample\\.[A-Z].*", // only in sample package
        excludesFqnRegex = "sample.Position"
    )
)
@ClassMembersTable(
    K2DSymbolSelectorAnnotation(
        includesFqnRegex = "sample\\.[A-Z].*", // only in sample package
    )
)
object DocumentationGenerator