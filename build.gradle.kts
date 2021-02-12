plugins {
    id("org.jlleitschuh.gradle.ktlint") version AndroidVersions.ktlintGradlePluginVersion
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.2")
        classpath(kotlin("gradle-plugin", version = AndroidVersions.kotlinVersion))
        classpath("org.jetbrains.kotlin:kotlin-serialization:${AndroidVersions.kotlinVersion}")
        classpath("de.mannodermaus.gradle.plugins:android-junit5:1.6.2.0")
        classpath("org.jlleitschuh.gradle:ktlint-gradle:${AndroidVersions.ktlintGradlePluginVersion}")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
    apply(plugin = ("org.jlleitschuh.gradle.ktlint"))
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}

ktlint {
    debug.set(true)
    verbose.set(true)
}
