plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization")
    id("kotlin-parcelize")
}

android {
    compileSdk = Versions.compileSdkVersion
    buildToolsVersion = Versions.buildToolsVersion
    defaultConfig {
        minSdk = Versions.minSdkVersion
        targetSdk = Versions.targetSdkVersion
        Versions.appVersionCode
        Versions.appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            ) // ktlint-disable max-line-length
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    api(project(path = ":DwitchCommon"))
    api(project(path = ":DwitchEngine"))
    api(project(path = ":DwitchModel"))

    // Logging
    implementation("org.tinylog:tinylog-api-kotlin:2.4.0-M1")

    // Dagger
    val daggerVersion = "2.38.1"
    api("com.google.dagger:dagger:$daggerVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")

    // RxJava
    implementation("com.jakewharton.rxrelay3:rxrelay:3.0.1")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:1.0-M1-1.4.0-rc")

    // Java-WebSocket
    implementation("org.java-websocket:Java-WebSocket:1.5.2")

    // Properties file reading lib (https://github.com/sksamuel/hoplite
    implementation("com.sksamuel.hoplite:hoplite-core:1.4.7")
    implementation("com.sksamuel.hoplite:hoplite-yaml:1.4.7")

    // Joda time
    api("joda-time:joda-time:2.10.10")

    // JUnit5
    val junitVersion = "5.7.1"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")

    // AssertJ
    testImplementation("org.assertj:assertj-core:3.20.2")

    // MockK
    val mockkVersion = "1.12.0"
    androidTestImplementation("io.mockk:mockk-android:$mockkVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<Test> {
    useJUnitPlatform()
}
