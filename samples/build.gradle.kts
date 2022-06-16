plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
    id("org.jetbrains.dokka") version "1.6.21"
}

dependencies {
    dokkaPlugin("com.glureau:html-mermaid-dokka-plugin:0.3.0")
}


kotlin {
    jvm()

    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlin.js.ExperimentalJsExport")
        }
        val commonMain by getting {
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
        dependencies.add(dokkaPlugin.name, "com.glureau:html-mermaid-dokka-plugin:0.3.0")
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