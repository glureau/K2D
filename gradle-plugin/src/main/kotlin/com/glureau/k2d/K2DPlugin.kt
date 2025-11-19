package com.glureau.k2d

import com.google.devtools.ksp.gradle.KspExtension
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.util.*

@Suppress("unused")
class K2DPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val ext = target.extensions.create("k2d", K2DExtension::class.java)
        target.afterEvaluate {
            val kspExt = target.extensions.getByType(KspExtension::class.java)
            val configStr = Json.encodeToString(ext.fullConfiguration())

            // B64 because gradle interprets that json like code and fails for some weird reasons...
            val b64 = Base64.getEncoder().encodeToString(configStr.encodeToByteArray())
            kspExt.arg("k2d.config", b64)
        }
    }
}