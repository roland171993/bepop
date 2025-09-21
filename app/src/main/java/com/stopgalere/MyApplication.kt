package com.stopgalere

import android.app.Application
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import com.onesignal.notifications.INotificationClickEvent
import com.onesignal.notifications.INotificationClickListener
import com.stopgalere.util.AppConstants.ONESIGNAL_APP_ID
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.EntryPoints
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import com.stopgalere.di.NotificationHandlerEntryPoint
import com.stopgalere.util.AppConstants.CASE_DEBUG
import com.stopgalere.util.AppConstants.TAG

/*
  Base Application class for Stop Galère.
   - Initializes OneSignal push
   - Hooks notification click -> NotificationClickHandler (online => deep link, offline => NoInternetActivity)
 */
@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initOneSignal()
        setupNotificationClickRouting()
    }

    private fun initOneSignal() {
        // Verbose logs only in debug builds
        OneSignal.Debug.logLevel = if (CASE_DEBUG) LogLevel.VERBOSE else LogLevel.NONE

        try {
            if (ONESIGNAL_APP_ID.isBlank()) {
                println("[$TAG] OneSignal App Id is blank. Push will be disabled.")
                return
            }
            OneSignal.initWithContext(this, ONESIGNAL_APP_ID)
            println("[$TAG] OneSignal initialized with AppId=$ONESIGNAL_APP_ID")
        } catch (t: Throwable) {
            println("[$TAG] Failed to initialize OneSignal: ${t.message}")
            t.printStackTrace()
        }
    }

    // Add a click listener:
    // - Extracts "jobId" from additionalData (e.g., {"jobId":"abc123"})
    // - Delegates to NotificationClickHandler which checks isOnline:
    // - online  -> open deep link stopgalere://job/{jobId}
    // - offline -> open NoInternetActivity (shows R.string.no_internet)
    private fun setupNotificationClickRouting() {
        // The SDK holds a weak reference to the listener (per their docs), so it won’t leak your Application.
        // It stays registered until app process death, or until GC reclaims it (because you don’t hold a strong reference).
        val listener = object : INotificationClickListener {
            override fun onClick(event: INotificationClickEvent) {
                val data = event.notification.additionalData
                val jobId = data?.optString("jobId")?.takeIf { it.isNotBlank() }

                if (jobId == null) {
                    if (CASE_DEBUG) {
                        println("[$TAG] Notification clicked without jobId in additionalData: $data")
                    }
                    return
                }

                runCatching {
                    val entryPoint = EntryPoints.get(
                        applicationContext,
                        NotificationHandlerEntryPoint::class.java
                    )
                    entryPoint.handler().onJobNotificationClicked(jobId)
                    println("[$TAG] Routed notification click for jobId=$jobId")
                }.onFailure { e ->
                    println("[$TAG] Failed to route notification click for jobId=$jobId: ${e.message}")
                    e.printStackTrace()
                }
            }
        }
        OneSignal.Notifications.addClickListener(listener)
    }
}
