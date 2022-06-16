package com.glureau.kflounder.gradleplugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.dokka.gradle.DokkaTaskPartial

@Suppress("Unused")
class KFlounderGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        println("KFlounderGradlePlugin -- 2")
        if (!target.plugins.hasPlugin("com.google.devtools.ksp")) {
            /**
             * Because KSP is linked to Kotlin, it's preferable to not force anything and let the user knows
             * we need this dependency to work.
             */
            error("This plugin cannot work without KSP applied on this project (${target.name})")
        }

        target.tasks
            .filter { it.name.contains("dokka", true) }
            .forEach {
                println("-- Found the task = ${it.name}")
                it.dependsOn("compileKotlinMetadata")
            }

        //target.dependencies.add("kspMetadata", target.project(":compiler"))
        target.afterEvaluate {
            println("KF: ${target.name} uses KSP? " + target.plugins.hasPlugin("com.google.devtools.ksp"))
            if (target.plugins.hasPlugin("com.google.devtools.ksp")) {
                println("KF: ${target.name} adds kspCommonMainMetadata...")
                dependencies {
                    add("kspCommonMainMetadata", "com.glureau.kflounder:compiler:0.1.0")
                }
            }
            target.subprojects.forEach {
                println("KF: ${it.name} uses KSP? " + it.plugins.hasPlugin("com.google.devtools.ksp"))
                if (it.plugins.hasPlugin("com.google.devtools.ksp")) {
                    println("KF: ${it.name} adds kspCommonMainMetadata...")
                    it.dependencies {
                        add("kspCommonMainMetadata", "com.glureau.kflounder:compiler:0.1.0")
                    }
                }
            }
        }
        target.includeDokkaSourceSets()
        target.afterEvaluate {
            includeDokkaSourceSets()
            /*
            target.subprojects.forEach { sub ->
                sub.includeDokkaSourceSets()
            }*/
        }

        // subprojects {
        //     afterEvaluate {
        //         val dokkaPlugin = this.configurations.findByName("dokkaPlugin")
        //         if (dokkaPlugin != null) {
        //             dependencies.add(dokkaPlugin.name, "org.jetbrains.dokka:versioning-plugin:$dokkaVersion")
        //             dependencies.add(dokkaPlugin.name, "com.glureau:html-mermaid-dokka-plugin:0.3.0")
        //         }
        //
        //         rootProject.allprojects.forEach { p ->
        //             val tree = fileTree(p.buildDir.absolutePath + "/generated/ksp/")
        //             tree.include("**/package.md")
        //             tree.include("**/module.md")
        //             p.tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
        //                 dokkaSourceSets {
        //                     configureEach {
        //                         includes.from(tree.files)
        //                     }
        //                 }
        //             }
        //         }
        //     }
        // }
    }

    private fun Project.includeDokkaSourceSets() {
        val tree = fileTree(buildDir.absolutePath + "/generated/ksp/")
        tree.include("**/package.md")
        tree.include("**/module.md")
        println("Configuring \"${name}\"")
        println(" - mermaid files: ${tree.files}")
        tasks.withType(DokkaTaskPartial::class.java).configureEach {
            this.dokkaSourceSets.configureEach {
                includes.from(tree.files)
            }
        }
    }
}