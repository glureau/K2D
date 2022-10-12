plugins {
    kotlin("jvm")
    id("com.gradle.plugin-publish") version "0.15.0"
    `java-gradle-plugin`
}

dependencies {
    val kspVersion: String by project
    //implementation("com.google.devtools.ksp:symbol-processing:$kspVersion")
    //implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:$kspVersion")
    implementation(project(":lib"))
    compileOnly(gradleApi())
}

// TODO : Unify naming
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
