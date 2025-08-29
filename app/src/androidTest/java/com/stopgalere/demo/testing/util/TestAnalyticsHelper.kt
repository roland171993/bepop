package com.stopgalere.demo.testing.util

import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsEvent
import com.google.samples.apps.nowinandroid.core.analytics.AnalyticsHelper

class TestAnalyticsHelper : AnalyticsHelper {

    private val events = mutableListOf<AnalyticsEvent>()
    override fun logEvent(event: AnalyticsEvent) {
        events.add(event)
    }

    fun hasLogged(event: AnalyticsEvent) = event in events
}
