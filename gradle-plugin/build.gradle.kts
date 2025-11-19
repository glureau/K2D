plugins {
    kotlin("jvm")
    id("com.gradle.plugin-publish") version "2.0.0"
    `java-gradle-plugin`
}

dependencies {
    val kspVersion: String by project
    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:$kspVersion")
    implementation(project(":lib"))
    api(project(":compiler"))
    compileOnly(gradleApi())
}

group = "com.glureau.k2d"
tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "Implementation-Title" to project.name,
            // Critical for the plugin to setup the compiler with the same version.
            "Implementation-Version" to project.version,
        )
    }
}

gradlePlugin {
    website = "https://github.com/glureau/k2d"
    vcsUrl = "https://github.com/glureau/k2d"

    plugins {
        create("k2d") {
            id = "com.glureau.k2d"
            implementationClass = "com.glureau.k2d.K2DPlugin"
            displayName = "Kotlin to Documentation"
            description = "Generate markdown content from Kotlin code"
            tags = listOf("k2d", "kotlin", "documentation")
        }
    }
}

kotlin {
    jvmToolchain(17)
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
