package foo

import K2DSymbolSelectorAnnotation
import MermaidGraph

@MermaidGraph(
    "Shapes23",
    symbolSelector = K2DSymbolSelectorAnnotation(
        includesFqnRegex = "sample\\.[A-Z].*", // only in sample package
        excludesFqnRegex = "sample.Position"
    )
)
object DocumentationGenerator