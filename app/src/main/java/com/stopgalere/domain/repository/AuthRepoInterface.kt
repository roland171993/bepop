package com.stopgalere.domain.repository

import com.stopgalere.data.remote.dto.AuthResponse
import com.stopgalere.data.remote.dto.UpdateProfileRequest
import com.stopgalere.data.remote.dto.UserDto
import com.stopgalere.data.remote.dto.UserResponse
import okhttp3.MultipartBody

/**
 * Contract for all authentication operations.
 * The ViewModel depends on this interface; the real implementation lives in
 * [com.stopgalere.data.repository.AuthRepository].
 * Test doubles implement this interface directly.
 */
interface AuthRepoInterface {

    /** Register a new local account.  Persists the returned JWT automatically. */
    suspend fun register(
        firstName: String,
        lastName:  String,
        age:       Int?,
        email:     String,
        phone:     String,
        password:  String
    ): AuthResponse

    /** Login with email + password.  Persists the returned JWT automatically. */
    suspend fun login(email: String, password: String): AuthResponse

    /**
     * Sign-in via Google or Apple using a Firebase ID token.
     * Persists the returned JWT automatically.
     */
    suspend fun oauthSignIn(firebaseIdToken: String): AuthResponse

    /** Returns the authenticated user's profile. */
    suspend fun getProfile(): UserDto

    /** Updates editable profile fields (firstName, lastName, age, phone). */
    suspend fun updateProfile(request: UpdateProfileRequest): UserDto

    /** Uploads a new profile photo and returns the updated user. */
    suspend fun uploadPhoto(photoPart: MultipartBody.Part): UserDto

    /** Clears the stored JWT (logout). */
    fun logout()

    /** Returns true if a JWT token is currently stored. */
    fun isLoggedIn(): Boolean
}
