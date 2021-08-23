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
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    implementation("androidx.room:room-runtime:2.4.0-alpha04")
    implementation("androidx.room:room-rxjava3:2.4.0-alpha04")
    kapt("androidx.room:room-compiler:2.4.0-alpha04")
    testImplementation("androidx.room:room-testing:2.4.0-alpha04")

    // RxJava
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")
    implementation("com.jakewharton.rxrelay3:rxrelay:3.0.1")

    // Dagger
    implementation("com.google.dagger:dagger-android-support:2.38.1")
    implementation("com.google.dagger:dagger-android:2.38.1")
    implementation("com.google.dagger:dagger:2.38.1")
    kapt("com.google.dagger:dagger-android-processor:2.38.1")
    kapt("com.google.dagger:dagger-compiler:2.38.1")
    kaptAndroidTest("com.google.dagger:dagger-compiler:2.38.1")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:1.0-M1-1.4.0-rc")

    // JUnit4 (for instrumented tests)
    testImplementation("junit:junit:4.13.2")

    // MockK
    testImplementation("io.mockk:mockk:1.12.0")
    androidTestImplementation("io.mockk:mockk-android:1.12.0")

    // AssertJ
    testImplementation("org.assertj:assertj-core:3.20.2")
    androidTestImplementation("org.assertj:assertj-core:3.20.2")

    // Android testing stuff
    androidTestImplementation("androidx.test:core:1.4.0")
    androidTestImplementation("androidx.test:core-ktx:1.4.0")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test:rules:1.4.0")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.3")
    androidTestUtil("androidx.test:orchestrator:1.4.0")
    testImplementation("android.arch.core:core-testing:1.1.1")

    // Joda time
    implementation("joda-time:joda-time:2.10.10")
}
