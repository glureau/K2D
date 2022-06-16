plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
    id("org.jetbrains.dokka") version "1.6.20"
    id("com.glureau.kflounder") version "0.1.0"
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
    //add("kspMetadata", project(":compiler"))
}