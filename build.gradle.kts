// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    val kotlinVersion = "1.7.21"
    id("com.android.application") version "8.1.0-alpha02" apply false
    id("com.android.library") version "8.1.0-alpha02" apply false
    id("org.jetbrains.kotlin.android") version kotlinVersion apply false
    kotlin("jvm") version "1.7.21" apply false
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.7.21"))
    }
}
