package com.stopgalere.domain.repository

import com.stopgalere.data.remote.dto.MessageDto
import com.stopgalere.data.remote.dto.RoomDto
import java.io.File

/**
 * Domain contract for the customer-support chat feature.
 *
 * REST operations are suspend functions.
 * Real-time events are delivered via SocketManager (injected separately).
 */
interface ChatRepoInterface {

    /** Returns the list of rooms visible to the current user. */
    suspend fun getRooms(): List<RoomDto>

    /**
     * Returns paginated messages for [roomId], oldest first.
     *
     * @param limit   max number of messages to return (default 30)
     * @param before  ISO timestamp cursor — returns messages older than this
     */
    suspend fun getMessages(
        roomId: String,
        limit:  Int?    = null,
        before: String? = null
    ): List<MessageDto>

    /**
     * Uploads [file] as a message attachment in [roomId].
     * Returns the created Message DTO.
     */
    suspend fun uploadFile(roomId: String, file: File, mimeType: String): MessageDto
}
