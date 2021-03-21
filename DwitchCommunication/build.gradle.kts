

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
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.2")
    implementation("org.slf4j:slf4j-android:1.7.30")

    // Dagger
    implementation("com.google.dagger:dagger:2.30.1")
    kapt("com.google.dagger:dagger-compiler:2.30.1")

    // RxJava
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")
    implementation("com.jakewharton.rxrelay3:rxrelay:3.0.0")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:1.0-M1-1.4.0-rc")

    // Java-WebSocket
    implementation("org.java-websocket:Java-WebSocket:1.5.1")
}
