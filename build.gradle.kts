import com.vanniktech.maven.publish.MavenPublishBaseExtension

buildscript {
    val kotlinVersion: String by project
    val mavenPublishVersion: String by project
    repositories {
        mavenCentral()
        maven(url = "https://raw.githubusercontent.com/glureau/K2D/mvn-repo")
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.vanniktech.maven.publish:com.vanniktech.maven.publish.gradle.plugin:$mavenPublishVersion")
    }
}

val localProperties = java.util.Properties().apply {
    val file = File(rootProject.rootDir, "local.properties")
    if (file.exists()) {
        load(java.io.FileInputStream(file))
    }
}

plugins {
    id("maven-publish")
    id("org.jlleitschuh.gradle.ktlint") version "14.0.1"
    id("com.glureau.grip") version "0.4.5"
    id("com.android.library") version "8.9.0" apply false
}

allprojects {
    group = "com.glureau.k2d"
    version = "0.4.5"

    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }
}

subprojects {
    apply(plugin = "com.vanniktech.maven.publish")
    extensions.getByType(MavenPublishBaseExtension::class).apply {
        publishToMavenCentral()
        signAllPublications()
        pom {
            name.set(project.name)
            description.set("Kotlin to Documentation")
            url.set("https://github.com/glureau/k2d")

            licenses {
                license {
                    name.set("The Apache Software License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    distribution.set("repo")
                }
            }

            developers {
                developer {
                    id.set("glureau")
                    name.set("Grégory Lureau")
                }
            }

            scm {
                connection.set("scm:git:git://github.com/glureau/k2d.git")
                url.set("https://github.com/glureau/k2d/tree/master")
            }
        }
    }
}

grip {
    files = fileTree(projectDir).apply {
        include("README.md")
    }
}
