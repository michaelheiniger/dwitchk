plugins {
    id("org.jetbrains.kotlin.jvm")
    id("java-library")
    kotlin("plugin.serialization")
}

dependencies {

    // Logging
    implementation("org.tinylog:tinylog-api-kotlin:2.2.1")
    implementation("org.tinylog:tinylog-impl:2.2.1")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:1.0-M1-1.4.0-rc")

    // JUnit5
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.1")

    // AssertJ
    testImplementation("org.assertj:assertj-core:3.19.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<Test> {
    useJUnitPlatform()
}
