plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("kotlin-android")
}

val composeVersion = "1.0.0-beta09"

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
        viewBinding = true// Enables Jetpack Compose for this module
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = true // Enables code shrinking, obfuscation, and optimization

            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        getByName("test").java.srcDirs("src/test/java", "src/testShared")
        getByName("androidTest").java.srcDirs("src/androidTest/java", "src/testShared")
    }

    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true"
        )
    }

    composeOptions {
        kotlinCompilerExtensionVersion = composeVersion
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

    // Android
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")

    // ####### Jetpack Compose #######
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.compose.foundation:foundation:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.material:material-icons-core:$composeVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")
    implementation("androidx.compose.runtime:runtime-livedata:$composeVersion")
    implementation("androidx.compose.runtime:runtime-rxjava3:$composeVersion")
    implementation("androidx.navigation:navigation-compose:2.4.0-alpha03")
    implementation("androidx.activity:activity-compose:1.3.0-beta02")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha07")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    androidTestImplementation("androidx.compose.ui:ui-test:$composeVersion")

    // Dagger
    implementation("com.google.dagger:dagger-android-support:2.35.1")
    implementation("com.google.dagger:dagger-android:2.35.1")
    implementation("com.google.dagger:dagger:2.35.1")
    kapt("com.google.dagger:dagger-android-processor:2.35.1")
    kapt("com.google.dagger:dagger-compiler:2.35.1")
    kaptAndroidTest("com.google.dagger:dagger-compiler:2.35.1")

    // Logging
    implementation("org.tinylog:tinylog-api-kotlin:2.2.1")
    implementation("org.tinylog:tinylog-impl:2.2.1")

    // RxJava
    implementation("com.jakewharton.rxrelay3:rxrelay:3.0.0")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")

    // Joda time
    implementation("joda-time:joda-time:2.10.10")

    // JUnit4 (For ViewModel unit tests and instrumented tests)
    testImplementation("junit:junit:4.13.2")

    // Android testing stuff
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.2")
    androidTestImplementation("androidx.test:core-ktx:1.3.0")
    androidTestImplementation("androidx.test:core:1.3.0")
    androidTestImplementation("androidx.test:rules:1.3.0")
    androidTestImplementation("androidx.test:runner:1.3.0")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
    androidTestUtil("androidx.test:orchestrator:1.4.0-beta02")

    // Espresso (needed for CounterIdlingResource)
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.3.0")

    // Required to use androidx.arch.core.executor.testing.InstantTaskExecutorRule in ViewModel unit tests
    testImplementation("android.arch.core:core-testing:1.1.1")

    // MockK
    testImplementation("io.mockk:mockk:1.11.0")

    // AssertJ
    testImplementation("org.assertj:assertj-core:3.19.0")
    androidTestImplementation("org.assertj:assertj-core:3.19.0")

    // Robolectric (for unit tests that log stuff)
    testImplementation("org.robolectric:robolectric:4.5.1")
}
