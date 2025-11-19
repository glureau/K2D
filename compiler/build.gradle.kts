plugins {
    kotlin("jvm")
    id("maven-publish")
    kotlin("plugin.serialization")
}

repositories {
    mavenCentral()
}

val kotlinVersion: String by project
val kspVersion: String by project

dependencies {

    implementation(project(":lib"))
    implementation("com.squareup:kotlinpoet:1.10.2") {
        exclude(module = "kotlin-reflect")
    }
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("com.squareup:kotlinpoet-ksp:2.2.0")
    implementation("com.google.devtools.ksp:symbol-processing:$kspVersion")
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:1.6.0")
    testImplementation("junit:junit:4.13.2")
    //testImplementation(kotlin("test"))
    testImplementation("org.junit.platform:junit-platform-runner:1.14.1")
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.1")
    testImplementation("com.approvaltests:approvaltests:25.7.0")
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}

kotlin {
    jvmToolchain(17)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
