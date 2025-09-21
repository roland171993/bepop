package com.stopgalere.util

object AppConstants {
    const val DATABASE_NAME = "app.db"

    const val DEV_URL = "http://192.168.100.22:3000/api/"

    const val PROD_URL = "https://stopgalere.rolandassoh.com/api/"

    const val CASE_DEBUG = false

    const val BASE_URL = PROD_URL
    const val NETWORK_TIMEOUT = 7_200_000L // 2 hours sometime We Have Bad Connection in Ivory Coast so it's take time

    // Paging
    const val STARTING_PAGE_INDEX = 1

    const val PAGE_SIZE = 15

    const val ONESIGNAL_APP_ID  ="25bb0fa0-4d24-488e-b65a-55cef95600eb"

    // SharedPreferences
    const val KEY_USER_TOKEN = "user_token"
    const val PREFS_NAME = "stop_galere_prefs"
    const val PREF_KEY_FIRST_LAUNCH = "first_launch"

    const val STRING_LENGTH_MAX = 225  // Avoid ManInTheMiddle attacks limit string length

    const val STRING_LENGTH_MIN = 3

    const val TAG = "STG"
}