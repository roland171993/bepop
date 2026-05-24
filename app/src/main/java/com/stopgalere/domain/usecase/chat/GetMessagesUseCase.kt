package com.stopgalere.domain.usecase.chat

import com.stopgalere.data.remote.dto.MessageDto
import com.stopgalere.domain.repository.ChatRepoInterface
import javax.inject.Inject

/**
 * Returns paginated message history for a support room.
 * Older messages are requested by passing an ISO [before] cursor.
 */
class GetMessagesUseCase @Inject constructor(
    private val repo: ChatRepoInterface
) {
    suspend operator fun invoke(
        roomId: String,
        limit:  Int?    = null,
        before: String? = null
    ): List<MessageDto> = repo.getMessages(roomId, limit, before)
}
