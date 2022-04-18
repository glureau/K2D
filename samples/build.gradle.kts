plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
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
    add("kspMetadata", project(":compiler"))
}