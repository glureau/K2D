plugins {
    kotlin("multiplatform")
    id("com.google.devtools.ksp")
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
    add("kspMetadata", project(":compiler"))
}