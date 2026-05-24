package com.stopgalere.domain.repository

import android.net.Uri
import com.stopgalere.data.remote.dto.GenerateCoverLetterRequest

interface AiRepoInterface {
    suspend fun generateCoverLetter(request: GenerateCoverLetterRequest): Result<String>
    suspend fun generateProfessionalPhoto(
        photoUri: Uri?,
        style: String,
        voiceDescription: String,
        withSuit: Boolean,
        provider: String,
        jobTitle: String,
        sendToEmail: String?
    ): Result<String>
}
