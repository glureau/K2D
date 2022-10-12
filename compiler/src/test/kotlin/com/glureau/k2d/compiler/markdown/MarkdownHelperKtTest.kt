package com.glureau.k2d.compiler.markdown

import org.approvaltests.Approvals
import org.junit.Test

internal class MarkdownHelperKtTest {

    @Test
    fun fo() {
        Approvals.verify(mdTable(
            headers = listOf("name", "type", "comments"),
            listOf("name", "String", ""),
            listOf("productUuid", "Int", "no comments"),
            listOf("ez", "List<String>", "a"),
        ))
    }
}