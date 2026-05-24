package com.stopgalere.data.remote

import com.stopgalere.data.remote.dto.AuthResponse
import com.stopgalere.data.remote.dto.CoverLettersResponse
import com.stopgalere.data.remote.dto.JobDetailResponse
import com.stopgalere.data.remote.dto.JobDto
import com.stopgalere.data.remote.dto.JobsResponse
import com.stopgalere.data.remote.dto.LoginRequest
import com.stopgalere.data.remote.dto.MessagesResponse
import com.stopgalere.data.remote.dto.OAuthRequest
import com.stopgalere.data.remote.dto.PhotoUploadResponse
import com.stopgalere.data.remote.dto.RegisterRequest
import com.stopgalere.data.remote.dto.RoomsResponse
import com.stopgalere.data.remote.dto.UpdateProfileRequest
import com.stopgalere.data.remote.dto.UploadFileResponse
import com.stopgalere.data.remote.dto.UserResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/*
  DATA layer — Retrofit interface.

  Auth endpoints:
    POST /auth/register           — local registration
    POST /auth/login              — local login
    POST /auth/oauth              — Google / Apple (Firebase ID token)
    GET  /auth/me                 — get profile        [Bearer token required]
    PUT  /auth/profile            — update profile     [Bearer token required]
    POST /auth/photo              — upload photo        [Bearer token required, multipart]

  Chat endpoints:
    GET  /chat/rooms                       — list rooms (own room for user, all for admin)
    GET  /chat/rooms/{roomId}/messages     — paginated message history
    POST /chat/rooms/{roomId}/files        — upload file attachment (multipart, field: "file")

  Jobs / Cover-letters endpoints: unchanged.
 */
interface ApiService {

    // ----------------------------------------------------------------
    // Auth
    // ----------------------------------------------------------------

    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    /** Google Sign-In or Apple Sign-In — pass the Firebase ID token. */
    @POST("auth/oauth")
    suspend fun oauthSignIn(@Body body: OAuthRequest): AuthResponse

    @GET("auth/me")
    suspend fun getMe(): UserResponse

    @PUT("auth/profile")
    suspend fun updateProfile(@Body body: UpdateProfileRequest): UserResponse

    /** field name must match backend: "photo" */
    @Multipart
    @POST("auth/photo")
    suspend fun uploadPhoto(@Part photo: MultipartBody.Part): PhotoUploadResponse

    // ----------------------------------------------------------------
    // Chat
    // ----------------------------------------------------------------

    @GET("chat/rooms")
    suspend fun getRooms(): RoomsResponse

    @GET("chat/rooms/{roomId}/messages")
    suspend fun getMessages(
        @Path("roomId") roomId: String,
        @Query("limit")  limit:  Int?    = null,
        @Query("before") before: String? = null
    ): MessagesResponse

    /** field name must match backend: "file" */
    @Multipart
    @POST("chat/rooms/{roomId}/files")
    suspend fun uploadChatFile(
        @Path("roomId") roomId: String,
        @Part file: MultipartBody.Part
    ): UploadFileResponse

    // ----------------------------------------------------------------
    // Jobs
    // ----------------------------------------------------------------

    @DELETE("jobs/{id}")
    suspend fun deleteJob(@Path("id") id: String)

    @GET("jobs")
    suspend fun getJobs(
        @Query("page")  page:  Int,
        @Query("query") query: String? = null
    ): JobsResponse

    @GET("jobs/{id}")
    suspend fun getJob(@Path("id") id: String): JobDetailResponse

    // ----------------------------------------------------------------
    // Cover Letters
    // ----------------------------------------------------------------

    @GET("cover-letters")
    suspend fun getCoverLetters(
        @Query("page")  page:  Int,
        @Query("query") query: String? = null
    ): CoverLettersResponse

    @DELETE("cover-letters/{id}")
    suspend fun deleteCoverLetter(@Path("id") id: String)

    // ── AI ────────────────────────────────────────────────────────────────────────
    @POST("ai/cover-letter")
    suspend fun generateCoverLetter(@Body request: com.stopgalere.data.remote.dto.GenerateCoverLetterRequest): com.stopgalere.data.remote.dto.GenerateCoverLetterResponse

    @Multipart
    @POST("ai/professional-photo")
    suspend fun generateProfessionalPhoto(
        @Part photo: okhttp3.MultipartBody.Part?,
        @Part("style") style: okhttp3.RequestBody,
        @Part("voiceDescription") voiceDescription: okhttp3.RequestBody,
        @Part("withSuit") withSuit: okhttp3.RequestBody,
        @Part("provider") provider: okhttp3.RequestBody,
        @Part("jobTitle") jobTitle: okhttp3.RequestBody,
        @Part("sendToEmail") sendToEmail: okhttp3.RequestBody?
    ): com.stopgalere.data.remote.dto.GenerateProPhotoResponse
}
