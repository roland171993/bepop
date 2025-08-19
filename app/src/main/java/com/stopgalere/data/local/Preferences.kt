package com.stopgalere.data.local

import android.content.Context
import com.stopgalere.util.AppConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Simple wrapper around SharedPreferences, moved from Preferences.kt → Prefs.kt
 */
class Prefs @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)

    fun getFirstLaunch(): Boolean = prefs.getBoolean(AppConstants.PREF_KEY_FIRST_LAUNCH, false)
    fun setFirstLaunch(value: Boolean) {
        prefs.edit().putBoolean(AppConstants.PREF_KEY_FIRST_LAUNCH, value).apply()
    }

}
