package com.stopgalere.data.remote.dto

import com.google.gson.annotations.SerializedName

// ─── Request bodies ───────────────────────────────────────────────────────────

data class RegisterRequest(
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName")  val lastName:  String,
    @SerializedName("age")       val age:        Int?,
    @SerializedName("email")     val email:      String,
    @SerializedName("phone")     val phone:      String,
    @SerializedName("password")  val password:   String
)

data class LoginRequest(
    @SerializedName("email")    val email:    String,
    @SerializedName("password") val password: String
)

/** Sent to POST /api/auth/oauth with the Firebase ID token. */
data class OAuthRequest(
    @SerializedName("idToken") val idToken: String
)

data class UpdateProfileRequest(
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName")  val lastName:  String?,
    @SerializedName("age")       val age:        Int?,
    @SerializedName("phone")     val phone:      String?
)

// ─── Response bodies ──────────────────────────────────────────────────────────

data class AuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("user")  val user:  UserDto
)

data class UserResponse(
    @SerializedName("user") val user: UserDto
)

data class PhotoUploadResponse(
    @SerializedName("photoUrl") val photoUrl: String,
    @SerializedName("user")     val user:     UserDto
)

// ─── User DTO (mirrors backend User.toPublic()) ───────────────────────────────

data class UserDto(
    @SerializedName("_id")          val id:           String,
    @SerializedName("firstName")    val firstName:    String,
    @SerializedName("lastName")     val lastName:     String,
    @SerializedName("age")          val age:          Int?,
    @SerializedName("email")        val email:        String,
    @SerializedName("phone")        val phone:        String,
    @SerializedName("photoUrl")     val photoUrl:     String?,
    @SerializedName("authProvider") val authProvider: String,
    @SerializedName("role")         val role:         String,
    @SerializedName("createdAt")    val createdAt:    String?
)
