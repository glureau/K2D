rootProject.name = "k2d"
pluginManagement {
    val kspVersion: String by settings
    val kotlinVersion: String by settings

    plugins {
        id("com.google.devtools.ksp") version kspVersion
        kotlin("plugin.serialization") version kotlinVersion
    }
    repositories {
        mavenLocal()
        gradlePluginPortal()
        google()
    }
}

include(":lib")
include(":compiler")
include(":gradle-plugin")
include(":samples")
