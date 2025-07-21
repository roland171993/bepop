package com.stopgalere

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Base Application class for Stop Galère.
 * Enables Hilt dependency injection.
 */
@HiltAndroidApp
class MyApplication : Application() {
    // You can override onCreate() here if you need to initialize
    // any SDKs, logging frameworks, etc. For example:
    //
    // override fun onCreate() {
    //   super.onCreate()
    //   // Timber.plant(Timber.DebugTree())
    // }
}
