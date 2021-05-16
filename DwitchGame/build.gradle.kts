plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization")
    id("kotlin-parcelize")
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
    implementation(project(path = ":DwitchCommon"))

    // Logging
    implementation("org.tinylog:tinylog-api-kotlin:2.4.0-M1")

    // Dagger
    implementation("com.google.dagger:dagger:2.35.1")
    kapt("com.google.dagger:dagger-compiler:2.35.1")
    kaptAndroidTest("com.google.dagger:dagger-compiler:2.35.1")

    // Logging
    implementation("org.tinylog:tinylog-api-kotlin:2.4.0-M1")

    // RxJava
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")
    implementation("com.jakewharton.rxrelay3:rxrelay:3.0.0")

    // Joda time
    implementation("joda-time:joda-time:2.10.10")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:1.0-M1-1.4.0-rc")

    // JUnit5
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.1")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.7.1")

    // MockK
    androidTestImplementation("io.mockk:mockk-android:1.11.0")
    testImplementation("io.mockk:mockk:1.11.0")

    // AssertJ
    testImplementation("org.assertj:assertj-core:3.19.0")
    androidTestImplementation("org.assertj:assertj-core:3.19.0")

    // JUnit4 (For instrumented tests)
    testImplementation("junit:junit:4.13.2")

    // AndroidJUnitRunner and JUnit Rules
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.2")
    androidTestImplementation("androidx.test:core-ktx:1.3.0")
    androidTestImplementation("androidx.test:core:1.3.0")
    androidTestImplementation("androidx.test:rules:1.3.0")
    androidTestImplementation("androidx.test:runner:1.3.0")
    androidTestUtil("androidx.test:orchestrator:1.4.0-alpha06")
    testImplementation("android.arch.core:core-testing:1.1.1")
}
