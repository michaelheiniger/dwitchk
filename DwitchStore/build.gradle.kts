plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization")
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

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf("room.schemaLocation" to "$projectDir/schemas")
            }
        }
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
    implementation(project(path = ":DwitchEngine"))
    implementation(project(path = ":DwitchModel"))

    // Logging
    implementation("org.tinylog:tinylog-api-kotlin:2.4.0-M1")

    // Room database
    val roomVersion = "2.4.0-alpha04"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-rxjava3:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    testImplementation("androidx.room:room-testing:$roomVersion")

    // RxJava
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")
    implementation("com.jakewharton.rxrelay3:rxrelay:3.0.1")

    // Dagger
    val daggerVersion = "2.38.1"
    implementation("com.google.dagger:dagger-android-support:$daggerVersion")
    implementation("com.google.dagger:dagger-android:$daggerVersion")
    implementation("com.google.dagger:dagger:$daggerVersion")
    kapt("com.google.dagger:dagger-android-processor:$daggerVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")
    kaptAndroidTest("com.google.dagger:dagger-compiler:$daggerVersion")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:1.0-M1-1.4.0-rc")

    // JUnit4 (for instrumented tests)
    testImplementation("junit:junit:4.13.2")

    // MockK
    val mockkVersion = "1.12.0"
    testImplementation("io.mockk:mockk:$mockkVersion")
    androidTestImplementation("io.mockk:mockk-android:$mockkVersion")

    // AssertJ
    val assertJVersion = "3.20.2"
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    androidTestImplementation("org.assertj:assertj-core:$assertJVersion")

    // Android testing stuff
    androidTestImplementation("androidx.test:core:1.4.0")
    androidTestImplementation("androidx.test:core-ktx:1.4.0")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test:rules:1.4.0")
    androidTestUtil("androidx.test:orchestrator:1.4.0")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.3")
    testImplementation("android.arch.core:core-testing:1.1.1")

    // Joda time
    implementation("joda-time:joda-time:2.10.10")
}
