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
            isMinifyEnabled = true
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
    kotlinOptions {
        jvmTarget = "11"
    }

    sourceSets {
        getByName("main").java.srcDirs("src/main/java")
        getByName("test").java.srcDirs("src/test/java", "src/testShared")
//        getByName("androidTest").java.srcDirs("src/androidTest/java", "src/testShared")
    }
}

dependencies {
    api(project(path = ":DwitchEngine"))
    api(project(path = ":DwitchModel"))
    api(project(path = ":DwitchStore"))
    api(project(path = ":DwitchCommunication"))
    api(project(path = ":DwitchCommon"))

    // Dagger
    api("com.google.dagger:dagger:2.44.2")
    kapt("com.google.dagger:dagger-compiler:2.44.2")
    kaptAndroidTest("com.google.dagger:dagger-compiler:2.44.2")

    // Logging
    implementation("org.tinylog:tinylog-api-kotlin:2.4.1")

    // RxJava
    implementation("com.jakewharton.rxrelay3:rxrelay:3.0.1")

    // Joda time
    implementation("joda-time:joda-time:2.10.10")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:1.0-M1-1.4.0-rc")

    // Properties file reading lib (https://github.com/sksamuel/hoplite
    implementation("com.sksamuel.hoplite:hoplite-core:1.4.7")
    implementation("com.sksamuel.hoplite:hoplite-yaml:1.4.7")

    // JUnit5
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.8.2")

    // MockK
    androidTestImplementation("io.mockk:mockk-android:1.12.0")
    testImplementation("io.mockk:mockk:1.12.1")

    // AssertJ
    testImplementation("org.assertj:assertj-core:3.21.0")
    androidTestImplementation("org.assertj:assertj-core:3.21.0")

    // AndroidJUnitRunner and JUnit Rules
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.4")
    androidTestImplementation("androidx.test:core-ktx:1.5.0")
    androidTestImplementation("androidx.test:core:1.5.0")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.1")
    androidTestUtil("androidx.test:orchestrator:1.4.2")
    testImplementation("android.arch.core:core-testing:1.1.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
