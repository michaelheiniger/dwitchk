plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("kotlin-android")
}

android {
    compileSdkVersion(Versions.compileSdkVersion)
    buildToolsVersion(Versions.buildToolsVersion)
    defaultConfig {
        applicationId = "ch.qscqlmpa.dwitch"
        minSdkVersion(Versions.minSdkVersion)
        targetSdkVersion(Versions.targetSdkVersion)
        versionCode = Versions.appVersionCode
        versionName = Versions.appVersionName

        testInstrumentationRunner = "ch.qscqlmpa.dwitch.app.CustomTestRunner"

        // Clears the app state between instrumented tests (activities, DB, ...)
        // Documentation: https://developer.android.com/training/testing/junit-runner
        testInstrumentationRunnerArguments(mapOf("clearPackageData" to "true"))
    }

    buildFeatures {
        compose = true // Enables Jetpack Compose for this module
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

    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }

    sourceSets {
        getByName("main").java.srcDirs("src/main/java")
        getByName("test").java.srcDirs("src/test/java", "src/testShared")
        getByName("androidTest").java.srcDirs("src/androidTest/java", "src/testShared")
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.0-beta01"
    }

    lint {
        isAbortOnError = false
    }

    // To prevent the error message (AndroidTest): " 2 files found with path 'META-INF/AL2.0' from inputs: ..."
    packagingOptions {
        resources.excludes.add("META-INF/AL2.0")
        resources.excludes.add("META-INF/LGPL2.1")
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

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.32") // Prevent compiler warning (implicit dependency)

    // Android / UI
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    implementation("com.google.android.material:material:1.3.0")

    // Dagger
    implementation("com.google.dagger:dagger-android-support:2.30.1")
    implementation("com.google.dagger:dagger-android:2.30.1")
    implementation("com.google.dagger:dagger:2.30.1")
    kapt("com.google.dagger:dagger-android-processor:2.30.1")
    kapt("com.google.dagger:dagger-compiler:2.30.1")
    kaptAndroidTest("com.google.dagger:dagger-compiler:2.30.1")

    // Logging
    implementation("org.tinylog:tinylog-api-kotlin:2.2.1")
    implementation("org.tinylog:tinylog-impl:2.2.1")

    // RxJava
    implementation("com.jakewharton.rxrelay3:rxrelay:3.0.0")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")

    // Joda time
    implementation("joda-time:joda-time:2.10.1")

    // JUnit4 (For ViewModel unit tests and instrumented tests)
    testImplementation("junit:junit:4.13.2")

    // Android testing stuff
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.2")
    androidTestImplementation("androidx.test:core-ktx:1.3.0")
    androidTestImplementation("androidx.test:core:1.3.0")
    androidTestImplementation("androidx.test:rules:1.3.0")
    androidTestImplementation("androidx.test:runner:1.3.0")
    androidTestUtil("androidx.test:orchestrator:1.4.0-alpha05")

    // Espresso (needed for CounterIdlingResource)
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.3.0")

    // To use androidx.arch.core.executor.testing.InstantTaskExecutorRule in ViewModel unit tests
    testImplementation("android.arch.core:core-testing:1.1.1")

    // MockK
    testImplementation("io.mockk:mockk:1.9.3")

    // AssertJ
    testImplementation("org.assertj:assertj-core:3.18.1")
    androidTestImplementation("org.assertj:assertj-core:3.18.1")

    // ####### Jetpack Compose #######
    val composeVersion = "1.0.0-beta05"
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.compose.foundation:foundation:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.material:material-icons-core:$composeVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")
    implementation("androidx.compose.runtime:runtime-livedata:$composeVersion")
    implementation("androidx.compose.runtime:runtime-rxjava3:$composeVersion")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.0.0-beta05")
    androidTestImplementation("androidx.compose.ui:ui-test:$composeVersion")

    implementation("androidx.activity:activity-compose:1.3.0-alpha07")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha04")

    // Robolectric (for unit tests that log stuff)
    testImplementation("org.robolectric:robolectric:4.5.1")

    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
}
