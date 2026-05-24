package com.stopgalere.data.repository

import android.util.Log
import com.stopgalere.data.remote.ApiService
import com.stopgalere.data.remote.dto.MessageDto
import com.stopgalere.data.remote.dto.RoomDto
import com.stopgalere.domain.repository.ChatRepoInterface
import com.stopgalere.util.AppConstants.TAG
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

/**
 * Production implementation of [ChatRepoInterface].
 * Delegates all REST calls to [ApiService].
 */
class ChatRepository @Inject constructor(
    private val api: ApiService
) : ChatRepoInterface {

    override suspend fun getRooms(): List<RoomDto> {
        Log.d(TAG, "ChatRepository.getRooms()")
        return api.getRooms().rooms
    }

    override suspend fun getMessages(
        roomId: String,
        limit:  Int?,
        before: String?
    ): List<MessageDto> {
        Log.d(TAG, "ChatRepository.getMessages(roomId=$roomId limit=$limit before=$before)")
        return api.getMessages(roomId, limit, before).messages
    }

    override suspend fun uploadFile(
        roomId:   String,
        file:     File,
        mimeType: String
    ): MessageDto {
        Log.d(TAG, "ChatRepository.uploadFile(roomId=$roomId file=${file.name} mime=$mimeType)")
        val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
        val part        = MultipartBody.Part.createFormData("file", file.name, requestBody)
        return api.uploadChatFile(roomId, part).message
    }
}
