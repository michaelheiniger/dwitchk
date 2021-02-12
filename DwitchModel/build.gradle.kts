plugins {
    id("org.jetbrains.kotlin.jvm")
    id("java-library")
    kotlin("kapt")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(path = ":DwitchEngine"))

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:1.0-M1-1.4.0-rc")

    // Joda time
    implementation("joda-time:joda-time:2.10.1")
}
