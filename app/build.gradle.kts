buildscript {
    repositories {
        google()
        mavenCentral()
    }
}


plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.RolandAssoh.stopgalere.ci"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.RolandAssoh.stopgalere.ci"
        minSdk = 24
        targetSdk = 35
        versionCode = 328
        versionName = "4.0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += setOf(
                "/META-INF/LICENSE.md",
                "/META-INF/LICENSE-notice.md",
                "/META-INF/NOTICE",
                "/META-INF/LICENSE",
                "/META-INF/{AL2.0,LGPL2.1}"
            )
        }
    }

}

kapt {
    correctErrorTypes = true
    arguments {
        arg("room.schemaLocation", "$projectDir/schemas")
        arg("room.incremental", "true")
        arg("room.expandProjection", "true")
    }
}

dependencies {
    val navComposeVersion         = "2.6.0"
    val hiltNavComposeVersion     = "1.0.0"
    val viewModelComposeVersion   = "2.6.1"
    val hiltVersion               = "2.56.2"
    val roomVersion               = "2.7.0"
    val retrofitVersion           = "2.9.0"
    val accompanistVersion        = "0.36.0"

    // Core Android
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

    // Jetpack Compose
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.runtime:runtime-livedata:1.5.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("androidx.compose.ui:ui-text-google-fonts:1.7.0-beta05")

    // Navigation-Compose & ViewModel-Compose
    implementation("com.google.dagger:hilt-android:${hiltVersion}")
    kapt("com.google.dagger:hilt-compiler:${hiltVersion}")
    implementation("androidx.navigation:navigation-compose:${navComposeVersion}")
    implementation("androidx.hilt:hilt-navigation-compose:${hiltNavComposeVersion}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:${viewModelComposeVersion}")
    implementation("androidx.compose.foundation:foundation:1.8.3")
    implementation("com.google.accompanist:accompanist-pager:${accompanistVersion}")
    implementation("com.google.accompanist:accompanist-pager-indicators:${accompanistVersion}")
    implementation("com.google.accompanist:accompanist-systemuicontroller:${accompanistVersion}")
    implementation("androidx.navigation:navigation-testing-android:2.9.3")

    implementation("br.com.devsrsouza.compose.icons:font-awesome:1.1.1")


    //Coil (image loading in Compose) ---
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Paging 3
    implementation ("androidx.paging:paging-runtime-ktx:3.3.0")
    implementation ("androidx.paging:paging-compose:3.3.0")

    // Room
    implementation ("androidx.room:room-ktx:${roomVersion}")
    implementation ("androidx.room:room-paging:${roomVersion}")
    kapt ("androidx.room:room-compiler:${roomVersion}")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Retrofit + OkHttp
    implementation("com.squareup.retrofit2:retrofit:${retrofitVersion}")
    implementation("com.squareup.retrofit2:converter-moshi:${retrofitVersion}")
    implementation("com.squareup.retrofit2:converter-gson:${retrofitVersion}")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")


    // Android Instrumentation Tests
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.00"))
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.56.2")
    // If you use MockK on androidTest:
    androidTestImplementation("io.mockk:mockk-android:1.13.11") {
        exclude(group = "org.junit.jupiter")
        exclude(group = "org.junit.platform")
    }

    // Hilt testing
    androidTestImplementation("com.google.dagger:hilt-android-testing:$hiltVersion")
    kaptAndroidTest("com.google.dagger:hilt-compiler:$hiltVersion")

    // Debug Only
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Unit / JVM Tests
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
}
