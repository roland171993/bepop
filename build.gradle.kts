// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Android Gradle Plugin
    id("com.android.application") version "8.10.0" apply false
    id("com.android.library")     version "8.10.0" apply false

    // Kotlin
    id("org.jetbrains.kotlin.android")        version "2.0.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false

    // Hilt Gradle plugin
    id("com.google.dagger.hilt.android") version "2.56.2" apply false
}