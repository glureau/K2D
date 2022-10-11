package foo

import ClassMembersTable
import K2DSymbolSelectorAnnotation
import MermaidGraph
import sample.Circle
import sample.Shape
import sample.Square

@MermaidGraph(
    "Shapes23",
    symbolSelector = K2DSymbolSelectorAnnotation(
        includesFqnRegex = "sample\\.[A-Z].*", // only in sample package
        excludesFqnRegex = "sample.Position",
    )
)
@ClassMembersTable(
    K2DSymbolSelectorAnnotation(
        includesFqnRegex = "sample\\.[A-Z].*", // only in sample package
        //includeChild = Order::class
        //klasses = [Circle::class, Square::class]
    )
)
object DocumentationGenerator