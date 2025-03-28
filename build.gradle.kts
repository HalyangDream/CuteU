import com.android.build.api.dsl.LibraryExtension

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.3.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    id("com.android.library") version "8.3.2" apply false
    id("androidx.room") version "2.6.1" apply false
}

buildscript {
    dependencies {
        classpath("com.alibaba:arouter-register:1.0.2")
        classpath("com.google.gms:google-services:4.4.2")
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.3")
    }
}

