plugins {
    kotlin("jvm")
    id("com.gradle.plugin-publish") version "0.15.0"
    `java-gradle-plugin`
}

group = "com.glureau.markdownreplace"
gradlePlugin {
    plugins {
        create("k2d") {
            // This is a fully-qualified plugin id, short id of 'kotlinx-knit' is added manually in resources
            id = "com.glureau.markdownreplace"
            implementationClass = "com.glureau.markdown_replace.MarkdownReplacePlugin"
            displayName = "Markdown replace tool"
            description = "Make dynamic changes in your markdown files"
        }
    }
}
