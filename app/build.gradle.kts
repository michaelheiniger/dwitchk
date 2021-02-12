plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("android.extensions")
}

android {
    compileSdkVersion(AndroidVersions.compileSdkVersion)
    buildToolsVersion(AndroidVersions.buildToolsVersion)
    defaultConfig {
        applicationId = "ch.qscqlmpa.dwitch"
        minSdkVersion(AndroidVersions.minSdkVersion)
        targetSdkVersion(AndroidVersions.targetSdkVersion)
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
    implementation(project(path = ":DwitchEngine"))
    implementation(project(path = ":DwitchModel"))
    implementation(project(path = ":DwitchStore"))
    implementation(project(path = ":DwitchCommunication"))
    implementation(project(path = ":DwitchGame"))
    implementation(project(path = ":DwitchCommon"))

    // common

    // Android
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.1.0")
    implementation("androidx.core:core-ktx:+")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.1.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${AndroidVersions.kotlinVersion}")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    // Dagger
    implementation("com.google.dagger:dagger:${Libs.daggerVersion}")
    kapt("com.google.dagger:dagger-compiler:${Libs.daggerVersion}")
    implementation("com.google.dagger:dagger-android:${Libs.daggerVersion}")
    implementation("com.google.dagger:dagger-android-support:${Libs.daggerVersion}")
    kapt("com.google.dagger:dagger-android-processor:${Libs.daggerVersion}")

    // Logging
    implementation("io.github.microutils:kotlin-logging-jvm:${Libs.kotlinLoggingVersion}")
    implementation("org.slf4j:slf4j-android:${Libs.slf4jVersion}")

    // RxJava
    implementation("io.reactivex.rxjava3:rxkotlin:${Libs.rxKotlinVersion}")
    implementation("io.reactivex.rxjava3:rxandroid:${Libs.rxAndroidVersion}")
    implementation("com.jakewharton.rxrelay3:rxrelay:${Libs.rxRelayVersion}")
    implementation("androidx.lifecycle:lifecycle-reactivestreams:${Libs.lifecycleReactiveStreamsVersion}")

    // Joda time
    implementation("joda-time:joda-time:${Libs.jodaVersion}")

    // ui
    implementation("com.google.android.material:material:1.2.1")
    implementation("androidx.constraintlayout:constraintlayout:2.0.0-beta5")
    implementation("androidx.recyclerview:recyclerview:${Libs.recyclerViewVersion}")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.appcompat:appcompat:1.0.2")
    implementation("com.jakewharton.rxbinding:rxbinding-kotlin:0.4.0")

    // test
    // JUnit4 (For ViewModel unit tests and instrumented tests)
    testImplementation("junit:junit:4.12")

    // MockK
    androidTestImplementation("io.mockk:mockk-android:1.9.3")
    testImplementation("io.mockk:mockk:1.9.3")

    testImplementation("org.assertj:assertj-core:${Libs.assertjVersion}")
    androidTestImplementation("org.assertj:assertj-core:${Libs.assertjVersion}")

    // Espresso
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.2.0")

    // AndroidJUnitRunner and JUnit Rules
    androidTestImplementation("androidx.test:core:1.3.0")
    androidTestImplementation("androidx.test:core-ktx:1.3.0")
    androidTestImplementation("androidx.test:runner:1.3.0")
    androidTestImplementation("androidx.test:rules:1.3.0")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.2")
    androidTestUtil("androidx.test:orchestrator:1.3.1-alpha02")

    // Dagger
    kaptAndroidTest("com.google.dagger:dagger-compiler:${Libs.daggerVersion}")

    // To use androidx.arch.core.executor.testing.InstantTaskExecutorRule in ViewModel unit tests
    testImplementation("android.arch.core:core-testing:1.1.1")
    testImplementation("androidx.room:room-testing:${Libs.roomVersion}")
}
