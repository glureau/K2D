import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.include

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka") version "2.1.0"
    id("com.google.devtools.ksp")
    id("com.glureau.k2d") version "0.4.6"
    id("com.glureau.grip") version "0.4.5"
}

repositories {
    maven(url = "https://raw.githubusercontent.com/glureau/K2D/mvn-repo")
    mavenCentral()
}

dependencies {
    dokkaPlugin("com.glureau:html-mermaid-dokka-plugin:0.6.0")
    ksp(project(":compiler"))
    kspCommonMainMetadata(project(":compiler"))
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
        commonTest {
            dependencies {
                //implementation(kotlin("test-common"))
                //implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("org.junit.platform:junit-platform-runner:1.14.1")
                implementation("org.junit.jupiter:junit-jupiter:6.0.1")
                implementation("com.approvaltests:approvaltests:25.7.0")
            }
        }
    }
}

afterEvaluate {
    val dokkaPlugin = this.configurations.findByName("dokkaPlugin")
    if (dokkaPlugin != null) {
        dependencies.add(dokkaPlugin.name, "com.glureau:html-mermaid-dokka-plugin:0.3.2")
    }
    val tree = fileTree(projectDir.absolutePath)
    // tree.include("/build/generated/ksp/**/*.md")
    tree.include("/build/grip/**/*.md")
    println("GREG - FILES ${tree.files}")
    dokka {
        dokkaSourceSets.commonMain {
            includes.from(tree.files)
            includes.from(tasks["grip"].outputs.files.asFileTree)
            println("GREG - INCLUDED ${tasks["grip"].outputs.files}")
        }
    }

    /*
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
    */
}

// Publish the sample documentation on branch "demo"
/*
gitPublish {
    publications.create("demo") {
        repoUri.set("git@github.com:glureau/K2D.git")
        branch.set("demo")
        contents.from("$buildDir/dokka/")
        preserve { include("**") }
        val head = grgit.head()
        commitMessage.set("${head.abbreviatedId}: ${project.version} : ${head.fullMessage}")
    }
}
*/

grip {
    files = fileTree(projectDir).apply {
        include("src/doc/**/*.md")
    }
    replaceInPlace = false // generates in /build/grip/
}
tasks["dokkaGeneratePublicationHtml"].dependsOn("grip")
tasks["dokkaGenerateHtml"].dependsOn("kspKotlinJvm")
tasks["dokkaGenerateHtml"].dependsOn("generateMetadataFileForKotlinMultiplatformPublication")
//tasks["gitPublishDemoCopy"].dependsOn("dokkaHtml")
tasks["jvmTest"].dependsOn("kspKotlinJvm")

/*
k2d {
    dokka {
        generateMermaidOnModules = false
        generateMermaidOnPackages = false
    }
    defaultMermaidConfiguration {
    }
    defaultMarkdownTableConfiguration {
    }
}
*/