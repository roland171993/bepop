package com.stopgalere.util

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.stopgalere.data.remote.NetworkMonitor
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import com.stopgalere.presentation.ui.common.NoInternetActivity

@Singleton
class NotificationClickHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val network: NetworkMonitor
) {
    fun onJobNotificationClicked(jobId: String) {
        // read the latest connectivity snapshot
        val online = runBlocking { network.isOnline.first() } // single read, safe from SDK callback
        if (online) {
            println("SEARCH onJobNotificationClicked cas Connected")
            val intent = Intent(
                Intent.ACTION_VIEW,
                "stopgalere://job/$jobId".toUri()
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            context.startActivity(intent)
        } else {
            println("SEARCH onJobNotificationClicked cas not Connected")
            val intent = Intent(context, NoInternetActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}
