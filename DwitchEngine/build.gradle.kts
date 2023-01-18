plugins {
    id("org.jetbrains.kotlin.jvm")
    id("java-library")
    kotlin("plugin.serialization")
}

dependencies {

    // Logging
    implementation("org.tinylog:tinylog-api-kotlin:2.4.1")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:1.0-M1-1.4.0-rc")

    // JUnit5
    val junitVersion = "5.7.1"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")

    // AssertJ
    testImplementation("org.assertj:assertj-core:3.21.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<Test> {
    useJUnitPlatform()
}
