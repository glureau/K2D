plugins {
    kotlin("jvm")
    id("com.gradle.plugin-publish") version "0.15.0"
    `java-gradle-plugin`
}

dependencies {
    val kspVersion: String by project
    //implementation("com.google.devtools.ksp:symbol-processing:$kspVersion")
    //implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
    implementation("com.google.devtools.ksp:symbol-processing-gradle-plugin:$kspVersion")
    implementation(project(":lib"))
    api(project(":compiler"))
    compileOnly(gradleApi())
}

// TODO : Unify naming
group = "com.glureau.k2d"
gradlePlugin {
    plugins {
        create("k2d") {
            // This is a fully-qualified plugin id, short id of 'kotlinx-knit' is added manually in resources
            id = "com.glureau.k2d"
            implementationClass = "com.glureau.k2d.K2DPlugin"
            displayName = "Kotlin to Documentation"
            description = "Generate markdown content from Kotlin code"
        }
    }
}

kotlin {
    jvmToolchain(11)
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "gradle-plugin"

            from(components["java"])
        }
    }
}
