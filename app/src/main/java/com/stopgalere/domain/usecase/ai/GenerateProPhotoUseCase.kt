package com.stopgalere.domain.usecase.ai

import android.net.Uri
import com.stopgalere.domain.repository.AiRepoInterface
import javax.inject.Inject

class GenerateProPhotoUseCase @Inject constructor(private val repo: AiRepoInterface) {
    suspend operator fun invoke(
        photoUri: Uri?, style: String, voiceDescription: String,
        withSuit: Boolean, provider: String, jobTitle: String, sendToEmail: String?
    ): Result<String> = repo.generateProfessionalPhoto(photoUri, style, voiceDescription, withSuit, provider, jobTitle, sendToEmail)
}
