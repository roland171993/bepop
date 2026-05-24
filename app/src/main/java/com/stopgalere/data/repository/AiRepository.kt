package com.stopgalere.data.repository

import android.content.Context
import android.net.Uri
import com.stopgalere.data.remote.ApiService
import com.stopgalere.data.remote.dto.GenerateCoverLetterRequest
import com.stopgalere.domain.repository.AiRepoInterface
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class AiRepository @Inject constructor(
    private val api: ApiService,
    private val context: Context
) : AiRepoInterface {

    override suspend fun generateCoverLetter(request: GenerateCoverLetterRequest): Result<String> =
        runCatching { api.generateCoverLetter(request).coverLetter }

    override suspend fun generateProfessionalPhoto(
        photoUri: Uri?,
        style: String,
        voiceDescription: String,
        withSuit: Boolean,
        provider: String,
        jobTitle: String,
        sendToEmail: String?
    ): Result<String> = runCatching {
        val photoPart = photoUri?.let {
            val inputStream = context.contentResolver.openInputStream(it)!!
            val tempFile = File.createTempFile("ai_photo_", ".jpg", context.cacheDir)
            FileOutputStream(tempFile).use { out -> inputStream.copyTo(out) }
            val requestBody = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("photo", tempFile.name, requestBody)
        }
        fun String.toRB() = toRequestBody("text/plain".toMediaTypeOrNull())
        api.generateProfessionalPhoto(
            photo = photoPart,
            style = style.toRB(),
            voiceDescription = voiceDescription.toRB(),
            withSuit = withSuit.toString().toRB(),
            provider = provider.toRB(),
            jobTitle = jobTitle.toRB(),
            sendToEmail = sendToEmail?.toRB()
        ).photoUrl
    }
}
