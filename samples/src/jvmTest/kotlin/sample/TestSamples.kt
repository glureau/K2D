package sample

import org.approvaltests.Approvals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
class TestSamples {

    @Test
    fun `check all generated files`() {
        val resourcePath = "build/generated/ksp/metadata/commonMain/resources"
        val generatedDir = File(resourcePath)
        println(generatedDir.absolutePath)
        var allMarkdownGenerated = ""
        generatedDir.onEachFile { file ->
            println("Testing ${file.absolutePath}")
            allMarkdownGenerated += "File: " + file.absolutePath.substringAfter("$resourcePath/") + "\n" + file.readText() + "\n"
        }

        Approvals.verify(allMarkdownGenerated)

        // Should be... but a space is generated 50% of the time from the library ??
        /*
        val files = mutableListOf<File>()
        generatedDir.onEachFile {
            files += it
        }
        CombinationApprovals.verifyAllCombinations({ it.readText().trim() }, files.toTypedArray())
         */
    }

    private fun File.onEachFile(action: (file: File) -> Unit) {
        listFiles().forEach { child ->
            if (child.isDirectory) {
                child.onEachFile(action)
            } else {
                action(child)
            }
        }
    }
}