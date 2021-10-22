plugins {
    kotlin("multiplatform") version "1.5.31"
    id("com.android.library")
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