

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("java-library")
    kotlin("kapt")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(path = ":DwitchCommon"))
    implementation(project(path = ":DwitchEngine"))
    implementation(project(path = ":DwitchModel"))

    // Logging
    implementation("org.tinylog:tinylog-api-kotlin:2.4.0-M1")

    // Dagger
    implementation("com.google.dagger:dagger:2.35.1")
    kapt("com.google.dagger:dagger-compiler:2.35.1")

    // RxJava
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")
    implementation("com.jakewharton.rxrelay3:rxrelay:3.0.0")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:1.0-M1-1.4.0-rc")

    // Java-WebSocket
    implementation("org.java-websocket:Java-WebSocket:1.5.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
