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
        jvmTarget = "11"
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

    // Dagger
    val daggerVersion = "2.38.1"
    implementation("com.google.dagger:dagger:$daggerVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")
    kaptAndroidTest("com.google.dagger:dagger-compiler:$daggerVersion")

    // Logging
    implementation("org.tinylog:tinylog-api-kotlin:2.4.0-M1")

    // RxJava
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")
    implementation("com.jakewharton.rxrelay3:rxrelay:3.0.1")

    // Joda time
    implementation("joda-time:joda-time:2.10.10")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:1.0-M1-1.4.0-rc")

    // Properties file reading lib (https://github.com/sksamuel/hoplite
    implementation("com.sksamuel.hoplite:hoplite-core:1.4.7")
    implementation("com.sksamuel.hoplite:hoplite-yaml:1.4.7")

    // JUnit5
    val jUnit5Version = "5.7.1"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$jUnit5Version")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$jUnit5Version")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$jUnit5Version")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:$jUnit5Version")

    // MockK
    val mockkVersion = "1.12.0"
    androidTestImplementation("io.mockk:mockk-android:$mockkVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")

    // AssertJ
    val assertJVersion = "3.20.2"
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    androidTestImplementation("org.assertj:assertj-core:$assertJVersion")

    // AndroidJUnitRunner and JUnit Rules
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.3")
    androidTestImplementation("androidx.test:core-ktx:1.4.0")
    androidTestImplementation("androidx.test:core:1.4.0")
    androidTestImplementation("androidx.test:rules:1.4.0")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestUtil("androidx.test:orchestrator:1.4.0")
    testImplementation("android.arch.core:core-testing:1.1.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
