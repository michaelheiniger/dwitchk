
plugins {
    id("org.jetbrains.kotlin.jvm")
    id("java-library")
    kotlin("plugin.serialization")
}

dependencies {

    // Logging
    implementation("io.github.microutils:kotlin-logging-jvm:${Libs.kotlinLoggingVersion}")
    implementation("org.slf4j:slf4j-android:${Libs.slf4jVersion}")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Libs.kotlinxSerializationVersion}")

//    test
    // JUnit5 (for unit tests except those of ViewModel)
    testImplementation("org.junit.jupiter:junit-jupiter-api:${Libs.junit5Version}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${Libs.junit5Version}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${Libs.junit5Version}")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:${Libs.junit5Version}")

    testImplementation("org.assertj:assertj-core:${Libs.assertjVersion}")
}
