// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}


buildscript {
    val kotlinVersion by extra("1.9.0") // Dichiarazione della versione di Kotlin
    val roomVersion by extra("2.5.1") // Dichiarazione della versione di Room

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:8.0.2")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

/*
allprojects {
    repositories {
        google()
        mavenCentral()
    }
} */
