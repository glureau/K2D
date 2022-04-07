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
            // KSP issue is limiting JS generation at the moment
            // https://github.com/google/ksp/issues/728
            /*
            kotlin.srcDir("build/generated/ksp/commonMain/kotlin")

            dependencies {
                implementation(project(":lib"))
            }*/
        }
    }
}

dependencies {
    add("kspJvm", project(":compiler"))
}