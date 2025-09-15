package com.stopgalere.util

object AppConstants {
    const val DATABASE_NAME = "app.db"
    const val DEV_URL = "http://192.168.1.6:3000/api/"

    const val PROD_URL = "changeMe"
    const val BASE_URL = DEV_URL
    const val NETWORK_TIMEOUT = 7_200_000L // 2 hours sometime We Have Bad Connection in Ivory Coast so it's take time

    // Paging
    const val STARTING_PAGE_INDEX = 1
    const val PAGE_SIZE = 15

    // SharedPreferences
    const val KEY_USER_TOKEN = "user_token"
    const val PREFS_NAME = "stop_galere_prefs"
    const val PREF_KEY_FIRST_LAUNCH = "first_launch"

    const val TAG = "JobsPaging"

}