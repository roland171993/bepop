package com.stopgalere.domain.usecase.chat

import com.stopgalere.data.remote.dto.MessageDto
import com.stopgalere.domain.repository.ChatRepoInterface
import java.io.File
import javax.inject.Inject

/** Uploads a file attachment (image or document) to a support room. */
class UploadChatFileUseCase @Inject constructor(
    private val repo: ChatRepoInterface
) {
    suspend operator fun invoke(
        roomId:   String,
        file:     File,
        mimeType: String
    ): MessageDto = repo.uploadFile(roomId, file, mimeType)
}
