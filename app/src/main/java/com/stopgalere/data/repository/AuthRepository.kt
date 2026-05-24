package com.stopgalere.data.repository

import android.util.Log
import com.stopgalere.data.local.Prefs
import com.stopgalere.data.remote.ApiService
import com.stopgalere.data.remote.dto.AuthResponse
import com.stopgalere.data.remote.dto.LoginRequest
import com.stopgalere.data.remote.dto.OAuthRequest
import com.stopgalere.data.remote.dto.RegisterRequest
import com.stopgalere.data.remote.dto.UpdateProfileRequest
import com.stopgalere.data.remote.dto.UserDto
import com.stopgalere.domain.repository.AuthRepoInterface
import com.stopgalere.util.AppConstants.TAG
import okhttp3.MultipartBody
import java.time.Instant
import javax.inject.Inject

/**
 * Production implementation of [AuthRepoInterface].
 *
 * All successful auth calls automatically persist the JWT in [Prefs] so the
 * OkHttp [com.stopgalere.util.AuthInterceptor] can pick it up on subsequent
 * requests without any extra coordination.
 *
 * Memory-leak note: only singleton dependencies are held — no Activity/Context.
 */
class AuthRepository @Inject constructor(
    private val api:   ApiService,
    private val prefs: Prefs
) : AuthRepoInterface {

    override suspend fun register(
        firstName: String,
        lastName:  String,
        age:       Int?,
        email:     String,
        phone:     String,
        password:  String
    ): AuthResponse {
        log("register email=$email")
        val response = api.register(
            RegisterRequest(firstName, lastName, age, email, phone, password)
        )
        prefs.saveToken(response.token)
        log("register success userId=${response.user.id}")
        return response
    }

    override suspend fun login(email: String, password: String): AuthResponse {
        log("login email=$email")
        val response = api.login(LoginRequest(email, password))
        prefs.saveToken(response.token)
        log("login success userId=${response.user.id}")
        return response
    }

    override suspend fun oauthSignIn(firebaseIdToken: String): AuthResponse {
        log("oauthSignIn")
        val response = api.oauthSignIn(OAuthRequest(firebaseIdToken))
        prefs.saveToken(response.token)
        log("oauthSignIn success userId=${response.user.id} provider=${response.user.authProvider}")
        return response
    }

    override suspend fun getProfile(): UserDto {
        log("getProfile")
        return api.getMe().user
    }

    override suspend fun updateProfile(request: UpdateProfileRequest): UserDto {
        log("updateProfile firstName=${request.firstName}")
        return api.updateProfile(request).user
    }

    override suspend fun uploadPhoto(photoPart: MultipartBody.Part): UserDto {
        log("uploadPhoto")
        return api.uploadPhoto(photoPart).user
    }

    override fun logout() {
        log("logout — clearing token")
        prefs.clearToken()
    }

    override fun isLoggedIn(): Boolean = prefs.isLoggedIn()

    // ----------------------------------------------------------------
    private fun log(msg: String) =
        Log.d(TAG, "[${Instant.now()}] [AuthRepository] $msg")
}
