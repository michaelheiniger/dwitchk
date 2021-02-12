plugins {
    id("org.jetbrains.kotlin.jvm")
    id("java-library")
    kotlin("kapt")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(path = ":DwitchEngine"))

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Libs.kotlinxSerializationVersion}")

    // Joda time
    implementation("joda-time:joda-time:${Libs.jodaVersion}")
}
