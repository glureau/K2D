package com.glureau.kflounder.gradleplugin

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("Unused")
class KFlounderGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        println("KFlounderGradlePlugin -- KFlounderGradlePlugin -- KFlounderGradlePlugin")
    }
}