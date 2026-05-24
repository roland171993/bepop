package com.stopgalere.util

import android.util.Log
import com.stopgalere.data.local.Prefs
import okhttp3.Interceptor
import okhttp3.Response
import java.time.Instant
import javax.inject.Inject

/**
 * OkHttp interceptor that attaches the stored JWT as a Bearer token on every
 * outgoing request.
 *
 * Requests to /auth/register, /auth/login, /auth/oauth are sent without a
 * token even if one is stored (they are public endpoints).
 *
 * Memory-leak note: [Prefs] is injected as a singleton; no Activity/Fragment
 * context is retained here.
 */
class AuthInterceptor @Inject constructor(
    private val prefs: Prefs
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path    = request.url.encodedPath

        // Skip auth header for public auth endpoints
        val isPublicAuth = path.contains("/auth/register") ||
                           path.contains("/auth/login")    ||
                           path.contains("/auth/oauth")

        val token = prefs.getToken()

        return if (!isPublicAuth && !token.isNullOrBlank()) {
            Log.d(AppConstants.TAG, "[${Instant.now()}] AuthInterceptor: attaching Bearer token for $path")
            chain.proceed(
                request.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            )
        } else {
            chain.proceed(request)
        }
    }
}
