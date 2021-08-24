plugins {
    id("org.jlleitschuh.gradle.ktlint-idea") version Versions.ktlintGradlePluginVersion
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.0-alpha09")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}")
        classpath("org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlinVersion}")
        classpath("de.mannodermaus.gradle.plugins:android-junit5:1.7.1.1")
        classpath("org.jlleitschuh.gradle:ktlint-gradle:${Versions.ktlintGradlePluginVersion}")

        val navVersion = "2.3.5"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
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
