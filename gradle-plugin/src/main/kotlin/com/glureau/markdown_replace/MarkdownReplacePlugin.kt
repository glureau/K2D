package com.glureau.markdown_replace

import K2DConfiguration
import K2DDokkaConfig
import com.google.devtools.ksp.gradle.KspExtension
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// TODO: Name this project ? Like K2D for "Kotlin to Documentation"? (not related to current file, general todo)
// TODO: Rename that for InPlaceReplace? It's not actually limited to Markdown...
class MarkdownReplacePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.create("markdownReplace", MarkdownReplaceExtension::class.java)
        target.tasks.create("markdownReplace", MarkdownReplaceTask::class.java) {
            it.description = "Replace content from markdown directives"
            it.group = "documentation"
        }
    }
}

open class MarkdownReplaceTask : DefaultTask() {
    init {
        configureKspCompiler(project)
    }

    @TaskAction
    fun execute() {
        val ext = project.extensions.getByType(MarkdownReplaceExtension::class.java)

        configureKspCompiler(project)

        val directives = listOf<Directive>(
            Directive("INSERT") { params -> "\n" + File(project.projectDir.absolutePath + "/" + params[0]).readText() },
            Directive("GRADLE_PROPERTIES") { params -> project.properties[params[0]].toString() },
            Directive("SYSTEM_ENV") { params -> System.getenv(params[0]) },
            Directive("DATETIME") { params -> SimpleDateFormat(params[0]).format(Calendar.getInstance().time) },
        )

        /**
         * Usually syntax of a directive is defined by :
         * <!--$ COMMAND some params -->
         * <!--$ END -->
         */

        ext.files.files.forEach { file ->
            val (tokenStart, tokenEnd) = getTokensFromFile(file)
            val fileContent = file.readText()
            val allMatches = tokenStart.findAll(fileContent).toList()

            if (allMatches.count() <= 1) return@forEach

            var updatedContent = fileContent.substring(0, allMatches.first().range.first)
            allMatches.forEachIndexed { index, startMatch ->
                val directiveWithParams = startMatch.groupValues[1]
                val endMatch = tokenEnd.find(fileContent, startIndex = startMatch.range.last) ?: error("boom")
                val endIndex = endMatch.range.first
                val oldContent = fileContent.substring(startMatch.range.last + 1, endIndex)
                updatedContent += startMatch.groupValues[0]
                val directiveKey = directiveWithParams.substringBefore(" ")
                val params = directiveWithParams.substringAfter(" ").trim()
                directives.firstOrNull { it.key == directiveKey }.let { d ->
                    if (d == null) {
                        println("Unknown directive $directiveKey ($directiveWithParams)")
                        updatedContent += oldContent
                    } else {
                        println("Execute directive $directiveWithParams")
                        // Split by " " is a bit tricky, may be specific to the current Directive instead
                        updatedContent += d.action(listOf(params))
                    }
                }

                updatedContent += fileContent.substring(
                    endMatch.range.start,
                    allMatches.getOrNull(index + 1)?.range?.start ?: fileContent.length
                )
            }

            val toFile = if (ext.replaceInPlace) file else File(project.buildDir.path + "/" + file.name)
            toFile.writeText(updatedContent)

            println("Final file:")
        }
    }

    private fun getTokensFromFile(file: File): Pair<Regex, Regex> {
        // Structure to handle tokens based on file extension
        val mermaidTokens = Regex("<!--\\$ (.*?)-->", RegexOption.DOT_MATCHES_ALL) to Regex("<!-- END \\$-->")
        return when {
            file.endsWith(".md") -> mermaidTokens
            else -> mermaidTokens
        }
    }

    private fun configureKspCompiler(project: Project) {
        val kspPlugin = project.plugins.getPlugin("com.google.devtools.ksp") ?: return
        val kspExt = project.extensions.getByType(KspExtension::class.java)
        println("Ksp Extension!")
        println(
            "setup arg = " + Json.encodeToString(
                K2DConfiguration(
                    dokkaConfig = K2DDokkaConfig(generateMermaidOnModules = true, generateMermaidOnPackages = false)
                )
            )
        )
        kspExt.arg(
            "k2d.config", Json.encodeToString(
                K2DConfiguration(
                    dokkaConfig = K2DDokkaConfig(generateMermaidOnModules = true, generateMermaidOnPackages = false)
                )
            )
        )
    }
}

data class Directive(val key: String, val action: (params: List<String>) -> String)

open class MarkdownReplaceExtension {
    var replaceInPlace: Boolean = false
    lateinit var files: FileCollection
}