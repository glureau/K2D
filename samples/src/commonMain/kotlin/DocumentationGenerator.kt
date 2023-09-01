@file:K2DMermaidGraph(
    name = "Shapes23",
    symbolSelector = K2DSymbolSelectorAnnotation(
        includesCurrentPackage = true,
        excludesClasses = [Position::class],
        excludesFqnRegex = "sample\\.[a-z]+.*", // only in sample package, given the convention package name
    )
)

@file:K2DMermaidGraph(
    name = "Shape_inheritance",
    symbolSelector = K2DSymbolSelectorAnnotation(
        includesCurrentPackage = false,
        includesClassesInheritingFrom = [Shape::class],
        excludesClasses = [Direction::class],
    ),
    configuration = K2DMermaidRendererConfiguration(
        showClassType = false,
        basicIsDown = true,
    )
)
@file:K2DMarkdownClassTable(
    symbolSelector = K2DSymbolSelectorAnnotation(
        includesCurrentPackage = true
    )
)

@file:K2DMarkdownClassTable(
    name = "Circle_no_functions",
    symbolSelector = K2DSymbolSelectorAnnotation(
        includesCurrentPackage = false,
        includesClasses = [Circle::class],
    ),
    configuration = K2DMarkdownClassTableConfiguration(
        showClassFunctions = false,
    )
)

package sample

import com.glureau.k2d.K2DMarkdownClassTable
import com.glureau.k2d.K2DMarkdownClassTableConfiguration
import com.glureau.k2d.K2DMermaidGraph
import com.glureau.k2d.K2DMermaidRendererConfiguration
import com.glureau.k2d.K2DSymbolSelectorAnnotation
import sample.enums.Direction
