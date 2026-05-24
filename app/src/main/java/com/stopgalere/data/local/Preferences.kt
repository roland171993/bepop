package com.stopgalere.data.local

import android.content.Context
import com.stopgalere.util.AppConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Thin SharedPreferences wrapper.
 * Stores app-level persistent flags and the auth JWT token.
 *
 * Security note: the JWT is stored in private-mode SharedPreferences.
 * For higher-security apps, use EncryptedSharedPreferences (Jetpack Security).
 */
class Prefs @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences(AppConstants.PREFS_NAME, Context.MODE_PRIVATE)

    // ----------------------------------------------------------------
    // First launch
    // ----------------------------------------------------------------
    fun getFirstLaunch(): Boolean  = prefs.getBoolean(AppConstants.PREF_KEY_FIRST_LAUNCH, false)
    fun setFirstLaunch(value: Boolean) {
        prefs.edit().putBoolean(AppConstants.PREF_KEY_FIRST_LAUNCH, value).apply()
    }

    // ----------------------------------------------------------------
    // Auth token
    // ----------------------------------------------------------------
    /** Persist the JWT returned by login / register / oauth. */
    fun saveToken(token: String) {
        prefs.edit().putString(AppConstants.KEY_USER_TOKEN, token).apply()
    }

    /** Returns the stored JWT, or null if the user is not logged in. */
    fun getToken(): String? = prefs.getString(AppConstants.KEY_USER_TOKEN, null)

    /** Returns true if a token is present (user considered authenticated). */
    fun isLoggedIn(): Boolean = !getToken().isNullOrBlank()

    /** Removes the token — call on logout. */
    fun clearToken() {
        prefs.edit().remove(AppConstants.KEY_USER_TOKEN).apply()
    }
}
