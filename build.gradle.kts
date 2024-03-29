plugins {
    id("org.jlleitschuh.gradle.ktlint-idea") version Versions.ktlintGradlePluginVersion
    id("io.gitlab.arturbosch.detekt").version(Versions.detektVersion)
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}")
        classpath("org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlinVersion}")
        classpath("de.mannodermaus.gradle.plugins:android-junit5:1.7.1.1")
        classpath("org.jlleitschuh.gradle:ktlint-gradle:${Versions.ktlintGradlePluginVersion}")
        classpath("com.google.gms:google-services:4.3.10")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.7.1")

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
    apply(plugin = ("io.gitlab.arturbosch.detekt"))
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${Versions.detektVersion}")
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}

ktlint {
    debug.set(true)
    verbose.set(true)
}

detekt {
    toolVersion = Versions.detektVersion
    config = files("config/detekt/detekt.yml")

    reports {
        xml {
            enabled = true
            destination = file("reports/detekt-report.xml")
        }
        html {
            enabled = true
            destination = file("reports/detekt-report.html")
        }
        txt {
            enabled = true
            destination = file("reports/detekt-report.txt")
        }
    }
}
