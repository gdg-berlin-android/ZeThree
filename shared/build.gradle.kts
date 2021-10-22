val kotlin_version: String by extra
plugins {
    kotlin("multiplatform") version "1.5.31"
    id("com.android.library")
}
apply {
    plugin("kotlin-android")
}

android {
    compileSdk = 31
}

kotlin {

    android()

    sourceSets {

        val commonMain  by getting {

        }

        val androidMain by getting {}

    }

}
dependencies {
    implementation("androidx.core:core-ktx:+")
    implementation(kotlinModule("stdlib-jdk7", kotlin_version))
}
repositories {
    mavenCentral()
}