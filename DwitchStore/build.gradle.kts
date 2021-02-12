plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization")
}

android {
    compileSdkVersion(AndroidVersions.compileSdkVersion)

    defaultConfig {
        minSdkVersion(AndroidVersions.minSdkVersion)
        targetSdkVersion(AndroidVersions.targetSdkVersion)
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
    implementation("io.github.microutils:kotlin-logging-jvm:${Libs.kotlinLoggingVersion}")
    implementation("org.slf4j:slf4j-android:${Libs.slf4jVersion}")

    // Room database
    implementation("androidx.room:room-runtime:${Libs.roomVersion}")
    implementation("androidx.room:room-rxjava3:${Libs.roomVersion}")
    kapt("androidx.room:room-compiler:${Libs.roomVersion}")

    // Dagger
    implementation("com.google.dagger:dagger:${Libs.daggerVersion}")
    kapt("com.google.dagger:dagger-compiler:${Libs.daggerVersion}")
    implementation("com.google.dagger:dagger-android:${Libs.daggerVersion}")
    implementation("com.google.dagger:dagger-android-support:${Libs.daggerVersion}")
    kapt("com.google.dagger:dagger-android-processor:${Libs.daggerVersion}")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Libs.kotlinxSerializationVersion}")

    //test
    // JUnit4 (For ViewModel unit tests and instrumented tests)
    testImplementation("junit:junit:4.12")

    // MockK
    testImplementation("io.mockk:mockk:1.9.3")
    androidTestImplementation("io.mockk:mockk-android:1.9.3")

    // AssertJ
    testImplementation("org.assertj:assertj-core:${Libs.assertjVersion}")
    androidTestImplementation("org.assertj:assertj-core:${Libs.assertjVersion}")

    // AndroidJUnitRunner and JUnit Rules
    androidTestImplementation("androidx.test:core:1.3.0")
    androidTestImplementation("androidx.test:core-ktx:1.3.0")
    androidTestImplementation("androidx.test:runner:1.3.0")
    androidTestImplementation("androidx.test:rules:1.3.0")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.2")
    androidTestUtil("androidx.test:orchestrator:1.3.1-alpha02")

    // Dagger
    kaptAndroidTest("com.google.dagger:dagger-compiler:${Libs.daggerVersion}")

    testImplementation("android.arch.core:core-testing:1.1.1")
    testImplementation("androidx.room:room-testing:${Libs.roomVersion}")

    // Joda time
    implementation("joda-time:joda-time:${Libs.jodaVersion}")
}
