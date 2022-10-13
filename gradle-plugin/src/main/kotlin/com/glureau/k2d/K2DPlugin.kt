package com.glureau.k2d

import com.google.devtools.ksp.gradle.KspExtension
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.api.Plugin
import org.gradle.api.Project

class K2DPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val ext = target.extensions.create("k2d", K2DExtension::class.java)
        target.afterEvaluate {
            val kspExt = target.extensions.getByType(KspExtension::class.java)
            kspExt.arg("k2d.config", Json.encodeToString(ext.config))
        }
    }
}