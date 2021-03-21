
plugins {
    id("org.jetbrains.kotlin.jvm")
    id("java-library")
    kotlin("plugin.serialization")
}

dependencies {

    // Logging
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.2")
    implementation("com.github.tony19:logback-android:2.0.0")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:1.0-M1-1.4.0-rc")

    // JUnit5
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.7.0")

    // AssertJ
    testImplementation("org.assertj:assertj-core:3.18.1")
}
