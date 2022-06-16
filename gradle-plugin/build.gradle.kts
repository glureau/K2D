plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    id("maven-publish")
}

repositories {
    maven(url = "https://plugins.gradle.org/m2/")
}

dependencies {
    val kspVersion: String by project
    val kotlinVersion: String by project
    implementation("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:$kspVersion")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")
    // https://mvnrepository.com/artifact/org.jetbrains.dokka/dokka-gradle-plugin
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.6.20")

    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    implementation("com.gradle.publish:plugin-publish-plugin:0.18.0")
}

gradlePlugin.plugins.create("kflounder") {
    id = "com.glureau.kflounder"
    version = project.version
    implementationClass = "com.glureau.kflounder.gradleplugin.KFlounderGradlePlugin"
    displayName = "KFlounder"
    description = "Setup the Mermaid diagram generation (from your code)"
}

/*
pluginBundle {
    description = "Setup the Mermaid diagram generation (from your code)"
    tags = listOf("kotlin", "mermaid", "ksp")
}


 */