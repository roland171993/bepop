plugins {
    alias(libs.plugins.android.application) // id("com.android.application")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.RolandAssoh.stopgalere.ci"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.RolandAssoh.stopgalere.ci"
        minSdk = 21
        targetSdk = 35
        versionCode = 328
        versionName = "4.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    val navComposeVersion         = "2.6.0"
    val hiltNavComposeVersion     = "1.0.0"
    val viewModelComposeVersion   = "2.6.1"

    // Core Android
    implementation(libs.androidx.core.ktx) // "androidx.core:core-ktx"
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation(libs.androidx.lifecycle.runtime.ktx) // "androidx.lifecycle:lifecycle-runtime-ktx:2.6.1"

    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom)) // Compose BOM
    implementation(libs.androidx.ui)               // "androidx.compose.ui:ui"
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)         // "androidx.compose.material3:material3"
    implementation(libs.androidx.activity.compose)  // "androidx.activity:activity-compose:1.8.0"
    implementation("androidx.compose.ui:ui-text-google-fonts:1.7.0-beta05")

    // Navigation-Compose & ViewModel-Compose
    implementation("com.google.dagger:hilt-android:2.56.2")
    kapt("com.google.dagger:hilt-compiler:2.56.2")
    implementation("androidx.navigation:navigation-compose:${navComposeVersion}")
    implementation("androidx.hilt:hilt-navigation-compose:${hiltNavComposeVersion}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:${viewModelComposeVersion}")

    // Retrofit / Room / Paging / Coroutines / etc.
    // TODO: add missing dependencies here

    // Android Instrumentation Tests
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.junit)               // "androidx.test.ext:junit"
    androidTestImplementation(libs.androidx.espresso.core)      // "androidx.test.espresso:espresso-core"
    androidTestImplementation(libs.androidx.ui.test.junit4)     // "androidx.compose.ui:ui-test-junit4"
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.56.2")
    kaptAndroidTest("com.google.dagger:hilt-compiler:2.56.2")

    // Debug Only
    debugImplementation(libs.androidx.ui.tooling)               // Compose tooling
    debugImplementation(libs.androidx.ui.test.manifest)         // Test manifest helper

    // Unit / JVM Tests
    testImplementation(libs.junit)                              // JUnit 4 or Jupiter as defined
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("org.mockito:mockito-inline:5.3.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3") // if using JUnit 5
}
