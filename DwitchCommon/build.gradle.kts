plugins {
    id("org.jetbrains.kotlin.jvm")
    id("java-library")
}

dependencies {

    // RxJava
    api("io.reactivex.rxjava3:rxjava:3.0.6")

    // Joda time
    api("joda-time:joda-time:2.10.10")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
