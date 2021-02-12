plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    kotlin("android.extensions")
    id("de.mannodermaus.android-junit5")
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
    implementation("io.github.microutils:kotlin-logging-jvm:${Libs.kotlinLoggingVersion}")
    implementation("org.slf4j:slf4j-android:${Libs.slf4jVersion}")

    // Dagger
    implementation("com.google.dagger:dagger:${Libs.daggerVersion}")
    kapt("com.google.dagger:dagger-compiler:${Libs.daggerVersion}")

    // Logging
    implementation("io.github.microutils:kotlin-logging-jvm:${Libs.kotlinLoggingVersion}")
    implementation("org.slf4j:slf4j-android:${Libs.slf4jVersion}")

    // RxJava
    implementation("io.reactivex.rxjava3:rxkotlin:${Libs.rxKotlinVersion}")
    implementation("com.jakewharton.rxrelay3:rxrelay:${Libs.rxRelayVersion}")

    // Joda time
    implementation("joda-time:joda-time:${Libs.jodaVersion}")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Libs.kotlinxSerializationVersion}")

//    test
    // JUnit5
    testImplementation("org.junit.jupiter:junit-jupiter-api:${Libs.junit5Version}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${Libs.junit5Version}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${Libs.junit5Version}")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:${Libs.junit5Version}")

    // MockK
    androidTestImplementation("io.mockk:mockk-android:1.9.3")
    testImplementation("io.mockk:mockk:1.9.3")

    testImplementation("org.assertj:assertj-core:${Libs.assertjVersion}")
    androidTestImplementation("org.assertj:assertj-core:${Libs.assertjVersion}")

    androidTestImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test:core:1.3.0")
    androidTestImplementation("androidx.test:core-ktx:1.3.0")
    androidTestImplementation("androidx.test:runner:1.3.0")
    androidTestImplementation("androidx.test:rules:1.3.0")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.2")
    androidTestUtil("androidx.test:orchestrator:1.3.1-alpha02")
    testImplementation("android.arch.core:core-testing:1.1.1")
    testImplementation("androidx.room:room-testing:${Libs.roomVersion}")

    // Dagger
    kaptAndroidTest("com.google.dagger:dagger-compiler:${Libs.daggerVersion}")
}
