package com.stopgalere.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Simple wrapper around SharedPreferences, moved from Preferences.kt → Prefs.kt
 */
class Prefs @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("stop_galere_prefs", Context.MODE_PRIVATE)

    fun getFirstLaunch(): Boolean = prefs.getBoolean("first_launch", false)
    fun setFirstLaunch(value: Boolean) {
        prefs.edit().putBoolean("first_launch", value).apply()
    }

}
