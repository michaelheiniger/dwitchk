plugins {
    id("org.jetbrains.kotlin.jvm")
    id("java-library")
    kotlin("kapt")
}

dependencies {

    // Dagger
    implementation("com.google.dagger:dagger:2.44.2")
    kapt("com.google.dagger:dagger-compiler:2.44.2")

    // RxJava
    api("io.reactivex.rxjava3:rxjava:3.1.5")

    // Joda time
    api("joda-time:joda-time:2.10.10")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
