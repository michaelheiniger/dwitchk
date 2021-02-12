

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("java-library")
    kotlin("kapt")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(path = ":DwitchEngine"))
    implementation(project(path = ":DwitchModel"))
    implementation(project(path = ":DwitchCommon"))

    // Logging
    implementation("io.github.microutils:kotlin-logging-jvm:${Libs.kotlinLoggingVersion}")
    implementation("org.slf4j:slf4j-android:${Libs.slf4jVersion}")

    // Dagger
    implementation("com.google.dagger:dagger:${Libs.daggerVersion}")
    kapt("com.google.dagger:dagger-compiler:${Libs.daggerVersion}")

    // RxJava
    implementation("io.reactivex.rxjava3:rxkotlin:${Libs.rxKotlinVersion}")
    implementation("com.jakewharton.rxrelay3:rxrelay:${Libs.rxRelayVersion}")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Libs.kotlinxSerializationVersion}")

    // Java-WebSocket
    implementation("org.java-websocket:Java-WebSocket:${Libs.websocketVersion}")
}
