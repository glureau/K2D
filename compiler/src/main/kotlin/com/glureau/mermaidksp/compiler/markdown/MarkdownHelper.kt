package com.glureau.mermaidksp.compiler.markdown

fun StringBuilder.appendMdH1(header: String) = append("# $header\n\n")
fun StringBuilder.appendMdH2(header: String) = append("## $header\n\n")
fun StringBuilder.appendMdH3(header: String) = append("### $header\n\n")
fun StringBuilder.appendMdH4(header: String) = append("#### $header\n\n")
fun StringBuilder.appendMdMermaid(mermaidContent: String) = append(mdBlock(mermaidContent, "mermaid"))

fun mdBlock(content: String, blockType: String = ""): String = """```$blockType
        |$content
        |```
        |
    """.trimMargin()
