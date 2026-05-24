package com.stopgalere.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.stopgalere.data.local.Prefs
import com.stopgalere.data.remote.dto.MessageDto
import com.stopgalere.data.remote.socket.SocketManager
import com.stopgalere.domain.usecase.chat.GetMessagesUseCase
import com.stopgalere.domain.usecase.chat.UploadChatFileUseCase
import com.stopgalere.presentation.ui.chat.ChatUiState
import com.stopgalere.util.AppConstants.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

/**
 * ViewModel for the customer-support chat screen.
 *
 * - Connects to Socket.io via [SocketManager] and mirrors incoming events into [uiState].
 * - Loads initial message history via [GetMessagesUseCase].
 * - Sends text messages directly via [SocketManager.sendMessage].
 * - Uploads file attachments via [UploadChatFileUseCase].
 * - Sends typing indicators with a 2-second debounce.
 *
 * Memory-leak note: no Activity/Context held; only singleton DI dependencies.
 */
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val socketManager:       SocketManager,
    private val getMessagesUseCase:  GetMessagesUseCase,
    private val uploadFileUseCase:   UploadChatFileUseCase,
    private val prefs:               Prefs
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var typingJob: Job? = null

    init {
        observeSocketEvents()
    }

    // ── Public actions ──────────────────────────────────────────────

    /**
     * Connects the socket (if not already connected) and joins [roomId].
     * Also loads the initial message history.
     */
    fun enterRoom(roomId: String) {
        _uiState.update { it.copy(roomId = roomId, isLoading = true) }
        socketManager.connect()
        socketManager.joinRoom(roomId)
        loadHistory(roomId)
    }

    /** Sends a text message to the current room via Socket.io. */
    fun sendMessage(content: String) {
        val roomId = _uiState.value.roomId ?: return
        if (content.isBlank()) return
        socketManager.sendMessage(roomId, content)
    }

    /** Uploads a file attachment and adds the returned message to the list. */
    fun uploadFile(file: File, mimeType: String) {
        val roomId = _uiState.value.roomId ?: return
        _uiState.update { it.copy(isSending = true) }
        viewModelScope.launch {
            try {
                val message = uploadFileUseCase(roomId, file, mimeType)
                // The socket broadcast will also deliver this message;
                // deduplication is handled in appendMessage().
                appendMessage(message)
            } catch (e: Exception) {
                Log.e(TAG, "uploadFile error: ${e.message}", e)
                FirebaseCrashlytics.getInstance().recordException(e)
                _uiState.update { it.copy(errorMessage = e.message) }
            } finally {
                _uiState.update { it.copy(isSending = false) }
            }
        }
    }

    /**
     * Sends a typing indicator; automatically stops after 2 seconds of inactivity.
     * Call on every keystroke.
     */
    fun onTyping() {
        val roomId = _uiState.value.roomId ?: return
        socketManager.sendTyping(roomId, true)
        typingJob?.cancel()
        typingJob = viewModelScope.launch {
            delay(2_000)
            socketManager.sendTyping(roomId, false)
        }
    }

    /** Marks all messages in the current room as read. */
    fun markRead() {
        val roomId = _uiState.value.roomId ?: return
        socketManager.markRead(roomId)
    }

    /** Clears the current error banner. */
    fun consumeError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /** Called when the screen is permanently dismissed — disconnects the socket. */
    fun leaveRoom() {
        typingJob?.cancel()
        socketManager.disconnect()
    }

    // ── Private ─────────────────────────────────────────────────────

    private fun observeSocketEvents() {
        viewModelScope.launch {
            socketManager.isConnected.collect { connected ->
                _uiState.update { it.copy(isConnected = connected) }
            }
        }
        viewModelScope.launch {
            socketManager.newMessages.collect { message ->
                appendMessage(message)
            }
        }
        viewModelScope.launch {
            socketManager.userTyping.collect { event ->
                if (event.sender == "support") {
                    _uiState.update { it.copy(remoteIsTyping = event.isTyping) }
                }
            }
        }
        viewModelScope.launch {
            socketManager.callIncoming.collect { offer ->
                _uiState.update { it.copy(incomingCall = offer) }
            }
        }
    }

    /** Clears a pending incoming call banner (after the user accepts, rejects, or dismisses). */
    fun consumeIncomingCall() {
        _uiState.update { it.copy(incomingCall = null) }
    }

    private fun loadHistory(roomId: String) {
        viewModelScope.launch {
            try {
                val messages = getMessagesUseCase(roomId)
                _uiState.update { it.copy(messages = messages, isLoading = false) }
                // Mark as read once history is loaded
                socketManager.markRead(roomId)
            } catch (e: Exception) {
                Log.e(TAG, "loadHistory error: ${e.message}", e)
                FirebaseCrashlytics.getInstance().recordException(e)
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    /** Appends [message] if it is not already present (deduplication by id). */
    private fun appendMessage(message: MessageDto) {
        _uiState.update { state ->
            if (state.messages.any { it.id == message.id }) state
            else state.copy(messages = state.messages + message)
        }
    }
}
