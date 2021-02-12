plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("android.extensions")
}

android {
    compileSdkVersion(Versions.compileSdkVersion)
    buildToolsVersion(Versions.buildToolsVersion)
    defaultConfig {
        applicationId = "ch.qscqlmpa.dwitch"
        minSdkVersion(Versions.minSdkVersion)
        targetSdkVersion(Versions.targetSdkVersion)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "ch.qscqlmpa.dwitch.app.CustomTestRunner"

        // Clears the app state between instrumented tests (activities, DB, ...)
        // Documentation: https://developer.android.com/training/testing/junit-runner
        testInstrumentationRunnerArguments(mapOf("clearPackageData" to "true"))
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = true // Enables code shrinking, obfuscation, and optimization

            // Enables resource shrinking performed by the Android Gradle plugin.
            isShrinkResources = true

            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    androidExtensions {
        isExperimental = true
    }

    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }

    sourceSets {
        getByName("main").java.srcDirs("src/main/java")
        getByName("test").java.srcDirs("src/test/java", "src/testShared")
        getByName("androidTest").java.srcDirs("src/androidTest/java", "src/testShared")
    }
}

dependencies {
    // Other modules
    implementation(project(path = ":DwitchCommon"))
    implementation(project(path = ":DwitchCommunication"))
    implementation(project(path = ":DwitchEngine"))
    implementation(project(path = ":DwitchGame"))
    implementation(project(path = ":DwitchModel"))
    implementation(project(path = ":DwitchStore"))

    // Android / UI
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0-alpha2")
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.0")
    implementation("androidx.recyclerview:recyclerview:1.2.0-beta01")
    implementation("com.google.android.material:material:1.3.0")
    implementation("com.jakewharton.rxbinding:rxbinding-kotlin:0.4.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlinVersion}")

    // Dagger
    implementation("com.google.dagger:dagger-android-support:2.30.1")
    implementation("com.google.dagger:dagger-android:2.30.1")
    implementation("com.google.dagger:dagger:2.30.1")
    kapt("com.google.dagger:dagger-android-processor:2.30.1")
    kapt("com.google.dagger:dagger-compiler:2.30.1")
    kaptAndroidTest("com.google.dagger:dagger-compiler:2.30.1")

    // Logging
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.2")
    implementation("org.slf4j:slf4j-android:1.7.21")

    // RxJava
    implementation("androidx.lifecycle:lifecycle-reactivestreams:2.3.0")
    implementation("com.jakewharton.rxrelay3:rxrelay:3.0.0")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")

    // Joda time
    implementation("joda-time:joda-time:2.10.1")

    // JUnit4 (For ViewModel unit tests and instrumented tests)
    testImplementation("junit:junit:4.13")

    // Android testing stuff
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.2")
    androidTestImplementation("androidx.test:core-ktx:1.3.0")
    androidTestImplementation("androidx.test:core:1.3.0")
    androidTestImplementation("androidx.test:rules:1.3.0")
    androidTestImplementation("androidx.test:runner:1.3.0")
    androidTestUtil("androidx.test:orchestrator:1.4.0-alpha04")

    // Espresso
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.3.0")

    // To use androidx.arch.core.executor.testing.InstantTaskExecutorRule in ViewModel unit tests
    testImplementation("android.arch.core:core-testing:1.1.1")
    testImplementation("androidx.room:room-testing:2.3.0-beta01")

    // MockK
    testImplementation("io.mockk:mockk:1.9.3")
    androidTestImplementation("io.mockk:mockk-android:1.9.3")

    // AssertJ
    testImplementation("org.assertj:assertj-core:3.18.1")
    androidTestImplementation("org.assertj:assertj-core:3.18.1")
}
