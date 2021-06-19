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
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(path = ":DwitchEngine"))
    implementation(project(path = ":DwitchModel"))

    implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlinVersion}") // Prevent compiler warning (implicit dependency)

    // Logging
    implementation("org.tinylog:tinylog-api-kotlin:2.4.0-M1")

    // Room database
    implementation("androidx.room:room-runtime:2.4.0-alpha03")
    implementation("androidx.room:room-rxjava3:2.4.0-alpha03")
    kapt("androidx.room:room-compiler:2.4.0-alpha03")
    testImplementation("androidx.room:room-testing:2.4.0-alpha03")

    // Dagger
    implementation("com.google.dagger:dagger-android-support:2.35.1")
    implementation("com.google.dagger:dagger-android:2.35.1")
    implementation("com.google.dagger:dagger:2.35.1")
    kapt("com.google.dagger:dagger-android-processor:2.35.1")
    kapt("com.google.dagger:dagger-compiler:2.35.1")
    kaptAndroidTest("com.google.dagger:dagger-compiler:2.35.1")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:1.0-M1-1.4.0-rc")

    // JUnit4 (for instrumented tests)
    testImplementation("junit:junit:4.13.2")

    // MockK
    testImplementation("io.mockk:mockk:1.11.0")
    androidTestImplementation("io.mockk:mockk-android:1.11.0")

    // AssertJ
    testImplementation("org.assertj:assertj-core:3.19.0")
    androidTestImplementation("org.assertj:assertj-core:3.19.0")

    // Android testing stuff
    androidTestImplementation("androidx.test:core:1.3.0")
    androidTestImplementation("androidx.test:core-ktx:1.3.0")
    androidTestImplementation("androidx.test:runner:1.3.0")
    androidTestImplementation("androidx.test:rules:1.3.0")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.2")
    androidTestUtil("androidx.test:orchestrator:1.4.0-beta02")
    testImplementation("android.arch.core:core-testing:1.1.1")

    // Joda time
    implementation("joda-time:joda-time:2.10.10")
}
