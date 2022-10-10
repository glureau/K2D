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
import java.lang.Integer.min

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
        println("Execute !")
        val ext = project.extensions.getByType(MarkdownReplaceExtension::class.java)
        println("ext= ${ext.runnable}")

        configureKspCompiler(project)

        val directives = listOf<Directive>(
            Directive("INSERT") { params -> File(project.projectDir.absolutePath + "/" + params[0]).readText() }
        )

        /**
         * Syntax of a directive is defined by :
         * <!--$ COMMAND some params -->
         * <!--$ END -->
         */
        val tokenEnd = "<!-- END \$-->"

        ext.files.files.forEach { file ->
            val fileContent = file.readText()
            val splittedFile = fileContent.split("<!--\$")
            println("Checking $file")
            if (splittedFile.count() <= 1) return@forEach

            var updatedContent = splittedFile.first()
            splittedFile.drop(1).map { split ->

                val endDirectiveIndex: Int = min(split.indexOf("\n"), split.indexOf("-->"))
                val endIndex = split.indexOf(tokenEnd)
                if (endIndex == -1) error("Missing '$tokenEnd' in the file $file, cannot determine the end of the directive")

                val directiveWithParams = split.substringBefore("-->").trim()
                updatedContent += "<!--\$ $directiveWithParams -->\n"

                val directiveWithParamsSplit = directiveWithParams.split(" ")
                directives.firstOrNull { it.key == directiveWithParamsSplit[0] }.let { d ->
                    if (d == null) {
                        updatedContent += split.substring(endDirectiveIndex, endIndex)
                    } else {
                        updatedContent += d.action(directiveWithParamsSplit.drop(1))
                    }
                }

                println("Execute directive $directiveWithParams")
                updatedContent += "\n$tokenEnd\n"
                updatedContent += split.substring(endIndex + tokenEnd.length)
            }

            val toFile = if (ext.replaceInPlace) file else File(project.buildDir.path + "/" + file.name)
            toFile.writeText(updatedContent)

            println("Final file:")
        }
    }

    private fun configureKspCompiler(project: Project) {
        val kspPlugin = project.plugins.getPlugin("com.google.devtools.ksp") ?: return
        val kspExt = project.extensions.getByType(KspExtension::class.java)
        println("Ksp Extension!")
        println("setup arg = " + Json.encodeToString(K2DConfiguration(
            dokkaConfig = K2DDokkaConfig(generateMermaidOnModules = true, generateMermaidOnPackages = false)
        )))
        kspExt.arg("k2d.config", Json.encodeToString(K2DConfiguration(
            dokkaConfig = K2DDokkaConfig(generateMermaidOnModules = true, generateMermaidOnPackages = false)
        )))
    }
}

data class Directive(val key: String, val action: (params: List<String>) -> String)

open class MarkdownReplaceExtension {
    var replaceInPlace: Boolean = false
    lateinit var files: FileCollection
    lateinit var runnable: Runnable
}