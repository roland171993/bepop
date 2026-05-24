package com.stopgalere.presentation.ui.chat

import com.stopgalere.data.remote.dto.CallOfferEvent
import com.stopgalere.data.remote.dto.MessageDto

/**
 * Immutable UI state for the chat screen.
 *
 * @param messages       Ordered list of messages (oldest first) for the current room.
 * @param isLoading      True while the initial message history is being fetched.
 * @param isSending      True while a text message or file upload is in flight.
 * @param isConnected    Reflects the underlying Socket.io connection state.
 * @param remoteIsTyping True when the other party is currently typing.
 * @param errorMessage   Non-null when an error should be shown to the user.
 * @param roomId         The current room identifier, or null before the room is joined.
 * @param incomingCall   Non-null when there is a pending incoming video call to accept or reject.
 */
data class ChatUiState(
    val messages:       List<MessageDto> = emptyList(),
    val isLoading:      Boolean          = false,
    val isSending:      Boolean          = false,
    val isConnected:    Boolean          = false,
    val remoteIsTyping: Boolean          = false,
    val errorMessage:   String?          = null,
    val roomId:         String?          = null,
    val incomingCall:   CallOfferEvent?  = null
)
