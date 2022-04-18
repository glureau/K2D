plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    id("maven-publish")
}

dependencies {
    val kspVersion: String by project
    val kotlinVersion: String by project
    implementation("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:$kspVersion")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")
}

gradlePlugin.plugins.create("kflounder") {
    id = "com.glureau.kflounder"
    implementationClass = "com.glureau.kflounder.gradleplugin.KFlounderGradlePlugin"
    displayName = "KFlounder"
    description = "Setup the Mermaid diagram generation (from your code)"
}
