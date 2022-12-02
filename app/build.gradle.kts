plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("kotlin-android")
//    id("com.google.gms.google-services")
//    id("com.google.firebase.crashlytics")
}

val composeVersion = "1.3.1"

android {
    compileSdk = Versions.compileSdkVersion
    buildToolsVersion = Versions.buildToolsVersion
    defaultConfig {
        applicationId = "ch.qscqlmpa.dwitch"
        minSdk = Versions.minSdkVersion
        targetSdk = Versions.targetSdkVersion
        versionCode = Versions.appVersionCode
        versionName = Versions.appVersionName

        testInstrumentationRunner = "ch.qscqlmpa.dwitch.app.CustomTestRunner"
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = true // Enables code shrinking, obfuscation, and optimization

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }

    sourceSets {
        getByName("main").java.srcDirs("src/main/java")
//        getByName("test").java.srcDirs("src/test/java", "src/testShared")
//        getByName("androidTest").java.srcDirs("src/androidTest/java", "src/testShared")
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = composeVersion
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

    // Android
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("com.google.android.material:material:1.7.0")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.datastore:datastore:1.0.0")

    // ####### Jetpack Compose #######
    implementation("androidx.compose.ui:ui:$composeVersion")

    // Required despite what dependency-analysis (see README) is saying
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")

    implementation("androidx.activity:activity-compose:1.6.1")
    implementation("androidx.compose.foundation:foundation:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.navigation:navigation-compose:2.6.0-alpha04")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("com.google.accompanist:accompanist-navigation-animation:0.21.0-beta")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.21.0-beta")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    androidTestImplementation("androidx.compose.ui:ui-test:$composeVersion")

    // Dagger
    implementation("com.google.dagger:dagger:2.44.2")
    kapt("com.google.dagger:dagger-compiler:2.44.2")
    kaptAndroidTest("com.google.dagger:dagger-compiler:2.44.2")

    // Logging
    implementation("org.tinylog:tinylog-api-kotlin:2.4.1")
    implementation("org.tinylog:tinylog-impl:2.4.1")

    // RxJava
    implementation("com.jakewharton.rxrelay3:rxrelay:3.0.1")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")

    // Joda time
    implementation("joda-time:joda-time:2.10.10")

    // JUnit4 (For ViewModel unit tests and instrumented tests)
    testImplementation("junit:junit:4.13.2")

    // Android testing stuff
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.4")
    androidTestImplementation("androidx.test:core-ktx:1.5.0")
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.1")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
    androidTestUtil("androidx.test:orchestrator:1.4.2")

    // Espresso (needed for CounterIdlingResource)
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.0")

    // Required to use androidx.arch.core.executor.testing.InstantTaskExecutorRule in ViewModel unit tests
    testImplementation("android.arch.core:core-testing:1.1.1")

    // MockK
    testImplementation("io.mockk:mockk:1.12.1")

    // AssertJ
    testImplementation("org.assertj:assertj-core:3.21.0")
    androidTestImplementation("org.assertj:assertj-core:3.21.0")

    // Robolectric (for unit tests that log stuff)
    testImplementation("org.robolectric:robolectric:4.5.1") // v4.6.1 produces weird error

//    implementation(platform("com.google.firebase:firebase-bom:28.4.1"))
//    implementation("com.google.firebase:firebase-analytics-ktx")
//    implementation("com.google.firebase:firebase-crashlytics-ktx")

    // QR code
    val cameraxVersion = "1.0.0-beta07"
    implementation("androidx.camera:camera-camera2:$cameraxVersion") // Required despite what's dependency-analysis (README) is saying
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:1.0.0-alpha14")
    implementation("com.google.zxing:core:3.4.1")
}
