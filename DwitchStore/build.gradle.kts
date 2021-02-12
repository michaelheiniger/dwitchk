plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization")
}

android {
    compileSdkVersion(Versions.compileSdkVersion)

    defaultConfig {
        minSdkVersion(Versions.minSdkVersion)
        targetSdkVersion(Versions.targetSdkVersion)
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(path = ":DwitchEngine"))
    implementation(project(path = ":DwitchModel"))

    // Logging
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.2")
    implementation("org.slf4j:slf4j-android:1.7.21")

    // Room database
    implementation("androidx.room:room-runtime:2.3.0-beta01")
    implementation("androidx.room:room-rxjava3:2.3.0-beta01")
    kapt("androidx.room:room-compiler:2.3.0-beta01")
    testImplementation("androidx.room:room-testing:2.3.0-beta01")

    // Dagger
    implementation("com.google.dagger:dagger-android-support:2.30.1")
    implementation("com.google.dagger:dagger-android:2.30.1")
    implementation("com.google.dagger:dagger:2.30.1")
    kapt("com.google.dagger:dagger-android-processor:2.30.1")
    kapt("com.google.dagger:dagger-compiler:2.30.1")
    kaptAndroidTest("com.google.dagger:dagger-compiler:2.30.1")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:1.0-M1-1.4.0-rc")

    // JUnit4 (For ViewModel unit tests and instrumented tests)
    testImplementation("junit:junit:4.13")

    // MockK
    testImplementation("io.mockk:mockk:1.9.3")
    androidTestImplementation("io.mockk:mockk-android:1.9.3")

    // AssertJ
    testImplementation("org.assertj:assertj-core:3.18.1")
    androidTestImplementation("org.assertj:assertj-core:3.18.1")

    // Android testing stuff
    androidTestImplementation("androidx.test:core:1.3.0")
    androidTestImplementation("androidx.test:core-ktx:1.3.0")
    androidTestImplementation("androidx.test:runner:1.3.0")
    androidTestImplementation("androidx.test:rules:1.3.0")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.2")
    androidTestUtil("androidx.test:orchestrator:1.4.0-alpha04")
    testImplementation("android.arch.core:core-testing:1.1.1")

    // Joda time
    implementation("joda-time:joda-time:2.10.1")
}
