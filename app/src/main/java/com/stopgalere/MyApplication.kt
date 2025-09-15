package com.stopgalere

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import com.stopgalere.util.AppConstants.ONESIGNAL_APP_ID

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
    override fun onCreate() {
        super.onCreate()

        // Enable verbose logging for debugging (remove in production)
        OneSignal.Debug.logLevel = LogLevel.VERBOSE
        // Initialize with your OneSignal App ID
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID)
        // Use this method to prompt for push notifications.
        // We recommend removing this method after testing and instead use In-App Messages to prompt for notification permission.
        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(true)
        }
    }
}
