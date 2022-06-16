import org.gradle.kotlin.dsl.gitPublish

plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
    id("org.jetbrains.dokka") version "1.6.21"
    id("org.ajoberstar.git-publish")
    id("org.ajoberstar.grgit")
}

dependencies {
    dokkaPlugin("com.glureau:html-mermaid-dokka-plugin:0.3.2")
}

kotlin {
    jvm()

    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlin.js.ExperimentalJsExport")
        }
        commonMain {
            dependencies {
                implementation(project(":lib"))
            }
        }
    }
}

dependencies {
    add("kspCommonMainMetadata", project(":compiler"))
}

afterEvaluate {
    val dokkaPlugin = this.configurations.findByName("dokkaPlugin")
    if (dokkaPlugin != null) {
        dependencies.add(dokkaPlugin.name, "com.glureau:html-mermaid-dokka-plugin:0.3.2")
    }
    val tree = fileTree(buildDir.absolutePath + "/generated/ksp/")
    tree.include("**/package.md")
    tree.include("**/module.md")

    tasks.withType<org.jetbrains.dokka.gradle.DokkaTask>().configureEach {
        dokkaSourceSets {
            configureEach {
                includes.from(tree.files)
            }
        }
    }
    tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
        dokkaSourceSets {
            configureEach {
                includes.from(tree.files)
            }
        }
    }
}

// Publish the sample documentation on branch "demo"
gitPublish {
    repoUri.set("git@github.com:glureau/MermaidKsp.git")
    branch.set("demo")
    contents.from("$buildDir/dokka/")
    preserve { include("**") }
    val head = grgit.head()
    commitMessage.set("${head.abbreviatedId}: ${project.version} : ${head.fullMessage}")
}

tasks["dokkaHtml"].dependsOn("generateMetadataFileForKotlinMultiplatformPublication")
tasks["gitPublishCopy"].dependsOn("dokkaHtml")
