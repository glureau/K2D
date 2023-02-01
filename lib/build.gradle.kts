plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
}

android {
    compileSdk = 33
    buildToolsVersion = "33.0.0"
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }
}

kotlin {
    js(IR) {
        browser()
        nodejs()
    }
    android { publishLibraryVariants("release", "debug") }
    jvm()
    ios()
    iosSimulatorArm64()
    tvos()
    watchos()
    macosArm64()
    macosX64()

    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlin.js.ExperimentalJsExport")
        }
    }

    targets.all {
        compilations.all {
            // Cannot enable rn due to native issue (stdlib included more than once)
            // may be related to https://youtrack.jetbrains.com/issue/KT-46636
            kotlinOptions.allWarningsAsErrors = false
        }
    }
}
