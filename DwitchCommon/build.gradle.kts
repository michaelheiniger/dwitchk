plugins {
    id("org.jetbrains.kotlin.jvm")
    id("java-library")
}

dependencies {

    // RxJava
    implementation("io.reactivex.rxjava3:rxjava:${Libs.rxJavaVersion}")
    implementation("io.reactivex.rxjava3:rxkotlin:${Libs.rxKotlinVersion}")
    implementation("com.jakewharton.rxrelay3:rxrelay:${Libs.rxRelayVersion}")

    // Joda time
    implementation("joda-time:joda-time:${Libs.jodaVersion}")
}
