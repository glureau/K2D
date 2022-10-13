@file:K2DMermaidGraph(
    "Shapes23",
    symbolSelector = K2DSymbolSelectorAnnotation(
        includesFqnRegex = "sample\\.[A-Z].*", // only in sample package
        excludesFqnRegex = "sample.Position",
    )
)


@file:K2DClassMembersTable(
    K2DSymbolSelectorAnnotation(
        includesFqnRegex = "sample\\..*", // only in sample package
    )
)

package foo

import com.glureau.k2d.K2DClassMembersTable
import com.glureau.k2d.K2DMermaidGraph
import com.glureau.k2d.K2DSymbolSelectorAnnotation
