package com.stopgalere

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import com.onesignal.notifications.INotificationClickEvent
import com.onesignal.notifications.INotificationClickListener
import com.stopgalere.util.AppConstants.CASE_DEBUG
import com.stopgalere.util.AppConstants.ONESIGNAL_APP_ID
import com.stopgalere.util.AppConstants.TAG
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.EntryPoints
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.stopgalere.di.NotificationHandlerEntryPoint

/*
  Base Application class for Stop Galère.
   - Initializes Firebase (Analytics + Crashlytics)
   - Initializes OneSignal push
   - Routes notification clicks to NotificationClickHandler
 */
@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initFirebase()
        initOneSignal()
        setupNotificationClickRouting()
    }

    // ----------------------------------------------------------------
    // Firebase — must be initialized before Crashlytics is used
    // ----------------------------------------------------------------
    private fun initFirebase() {
        try {
            FirebaseApp.initializeApp(this)

            // Disable automatic data collection in debug builds to avoid
            // polluting production Crashlytics data.
            FirebaseCrashlytics.getInstance().apply {
                isCrashlyticsCollectionEnabled = !CASE_DEBUG
            }

            log("Firebase + Crashlytics initialized (collection=${!CASE_DEBUG})")
        } catch (t: Throwable) {
            // Don't crash the app if Firebase fails to init (e.g. missing google-services.json)
            android.util.Log.e(TAG, "[${now()}] Failed to initialize Firebase: ${t.message}", t)
        }
    }

    // ----------------------------------------------------------------
    // OneSignal
    // ----------------------------------------------------------------
    private fun initOneSignal() {
        OneSignal.Debug.logLevel = if (CASE_DEBUG) LogLevel.VERBOSE else LogLevel.NONE
        try {
            if (ONESIGNAL_APP_ID.isBlank()) {
                log("OneSignal App Id is blank. Push will be disabled.")
                return
            }
            OneSignal.initWithContext(this, ONESIGNAL_APP_ID)
            log("OneSignal initialized AppId=$ONESIGNAL_APP_ID")
        } catch (t: Throwable) {
            // Report to Crashlytics but keep the app alive
            FirebaseCrashlytics.getInstance().recordException(t)
            android.util.Log.e(TAG, "[${now()}] Failed to initialize OneSignal: ${t.message}", t)
        }
    }

    // ----------------------------------------------------------------
    // Notification deep-link routing
    // ----------------------------------------------------------------
    private fun setupNotificationClickRouting() {
        val listener = object : INotificationClickListener {
            override fun onClick(event: INotificationClickEvent) {
                val data  = event.notification.additionalData
                val jobId = data?.optString("jobId")?.takeIf { it.isNotBlank() }

                if (jobId == null) {
                    if (CASE_DEBUG) log("Notification clicked without jobId in additionalData: $data")
                    return
                }

                runCatching {
                    val entryPoint = EntryPoints.get(
                        applicationContext,
                        NotificationHandlerEntryPoint::class.java
                    )
                    entryPoint.handler().onJobNotificationClicked(jobId)
                    log("Routed notification click jobId=$jobId")
                }.onFailure { e ->
                    FirebaseCrashlytics.getInstance().recordException(e)
                    android.util.Log.e(TAG, "[${now()}] Failed to route notification click jobId=$jobId: ${e.message}", e)
                }
            }
        }
        OneSignal.Notifications.addClickListener(listener)
    }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------
    private fun now() = java.time.Instant.now().toString()

    private fun log(msg: String) {
        android.util.Log.d(TAG, "[${now()}] $msg")
    }
}
