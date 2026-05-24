package com.stopgalere.data.remote.socket

import android.util.Log
import com.google.gson.Gson
import com.stopgalere.data.local.Prefs
import com.stopgalere.data.remote.dto.CallAnswerEvent
import com.stopgalere.data.remote.dto.CallEndEvent
import com.stopgalere.data.remote.dto.CallOfferEvent
import com.stopgalere.data.remote.dto.CallRejectEvent
import com.stopgalere.data.remote.dto.IceCandidateDto
import com.stopgalere.data.remote.dto.IceCandidateEvent
import com.stopgalere.data.remote.dto.MessageDto
import com.stopgalere.data.remote.dto.MessagesReadEvent
import com.stopgalere.data.remote.dto.NewMessageEvent
import com.stopgalere.data.remote.dto.RoomDto
import com.stopgalere.data.remote.dto.RoomJoinedEvent
import com.stopgalere.data.remote.dto.UserTypingEvent
import com.stopgalere.util.AppConstants
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SocketManager
 *
 * Manages a single Socket.io connection for the customer-support chat.
 *
 * Lifecycle:
 *   - connect()    → call when the user opens the chat screen (or on app start if logged in)
 *   - disconnect() → call when the user logs out or the app goes to background
 *
 * Events emitted to server:
 *   join_room    { roomId }
 *   send_message { roomId, content }
 *   mark_read    { roomId }
 *   typing       { roomId, isTyping }
 *
 * Events received from server (exposed as StateFlow / SharedFlow):
 *   new_message   → newMessages SharedFlow
 *   messages_read → messagesRead SharedFlow
 *   user_typing   → userTyping SharedFlow
 *   room_joined   → currentRoom StateFlow
 */
@Singleton
class SocketManager @Inject constructor(
    private val prefs: Prefs
) {
    companion object {
        private const val TAG = "SocketManager"

        // Server URL without /api — Socket.io connects at the root
        private val SERVER_URL = if (AppConstants.CASE_DEBUG)
            "http://192.168.100.22:3000"
        else
            "https://stopgalere.rolandassoh.com"
    }

    private val gson = Gson()

    private var socket: Socket? = null

    // ── Exposed state / events ──────────────────────────────────────

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _currentRoom = MutableStateFlow<RoomDto?>(null)
    val currentRoom: StateFlow<RoomDto?> = _currentRoom.asStateFlow()

    private val _newMessages = MutableSharedFlow<MessageDto>(extraBufferCapacity = 64)
    val newMessages: SharedFlow<MessageDto> = _newMessages.asSharedFlow()

    private val _messagesRead = MutableSharedFlow<MessagesReadEvent>(extraBufferCapacity = 8)
    val messagesRead: SharedFlow<MessagesReadEvent> = _messagesRead.asSharedFlow()

    private val _userTyping = MutableSharedFlow<UserTypingEvent>(extraBufferCapacity = 8)
    val userTyping: SharedFlow<UserTypingEvent> = _userTyping.asSharedFlow()

    // ── WebRTC signaling flows ──────────────────────────────────────

    private val _callIncoming = MutableSharedFlow<CallOfferEvent>(extraBufferCapacity = 4)
    val callIncoming: SharedFlow<CallOfferEvent> = _callIncoming.asSharedFlow()

    private val _callAnswered = MutableSharedFlow<CallAnswerEvent>(extraBufferCapacity = 4)
    val callAnswered: SharedFlow<CallAnswerEvent> = _callAnswered.asSharedFlow()

    private val _callRejected = MutableSharedFlow<CallRejectEvent>(extraBufferCapacity = 4)
    val callRejected: SharedFlow<CallRejectEvent> = _callRejected.asSharedFlow()

    private val _callEnded = MutableSharedFlow<CallEndEvent>(extraBufferCapacity = 4)
    val callEnded: SharedFlow<CallEndEvent> = _callEnded.asSharedFlow()

    private val _remoteIceCandidate = MutableSharedFlow<IceCandidateEvent>(extraBufferCapacity = 64)
    val remoteIceCandidate: SharedFlow<IceCandidateEvent> = _remoteIceCandidate.asSharedFlow()

    // ── Connection lifecycle ────────────────────────────────────────

    /**
     * Opens the Socket.io connection.
     * Idempotent — does nothing if already connected.
     */
    fun connect() {
        if (socket?.connected() == true) return

        val token = prefs.getToken() ?: run {
            Log.w(TAG, "connect() called without a stored JWT — skipping")
            return
        }

        try {
            val options = IO.Options.builder()
                .setAuth(mapOf("token" to token))
                .build()

            socket = IO.socket(SERVER_URL, options).apply {
                on(Socket.EVENT_CONNECT) {
                    Log.i(TAG, "Socket connected id=${id()}")
                    _isConnected.tryEmit(true)
                }
                on(Socket.EVENT_DISCONNECT) { args ->
                    Log.i(TAG, "Socket disconnected reason=${args.firstOrNull()}")
                    _isConnected.tryEmit(false)
                }
                on(Socket.EVENT_CONNECT_ERROR) { args ->
                    Log.e(TAG, "Socket connect error: ${args.firstOrNull()}")
                    _isConnected.tryEmit(false)
                }
                on("room_joined") { args ->
                    val json = args.firstOrNull() as? JSONObject ?: return@on
                    val event = gson.fromJson(json.toString(), RoomJoinedEvent::class.java)
                    _currentRoom.tryEmit(event.room)
                }
                on("new_message") { args ->
                    val json = args.firstOrNull() as? JSONObject ?: return@on
                    val event = gson.fromJson(json.toString(), NewMessageEvent::class.java)
                    _newMessages.tryEmit(event.message)
                }
                on("messages_read") { args ->
                    val json = args.firstOrNull() as? JSONObject ?: return@on
                    val event = gson.fromJson(json.toString(), MessagesReadEvent::class.java)
                    _messagesRead.tryEmit(event)
                }
                on("user_typing") { args ->
                    val json = args.firstOrNull() as? JSONObject ?: return@on
                    val event = gson.fromJson(json.toString(), UserTypingEvent::class.java)
                    _userTyping.tryEmit(event)
                }

                // ── WebRTC signaling listeners ────────────────────────────
                on("call_incoming") { args ->
                    val json = args.firstOrNull() as? JSONObject ?: return@on
                    val event = gson.fromJson(json.toString(), CallOfferEvent::class.java)
                    _callIncoming.tryEmit(event)
                }
                on("call_answered") { args ->
                    val json = args.firstOrNull() as? JSONObject ?: return@on
                    val event = gson.fromJson(json.toString(), CallAnswerEvent::class.java)
                    _callAnswered.tryEmit(event)
                }
                on("call_rejected") { args ->
                    val json = args.firstOrNull() as? JSONObject ?: return@on
                    val event = gson.fromJson(json.toString(), CallRejectEvent::class.java)
                    _callRejected.tryEmit(event)
                }
                on("call_ended") { args ->
                    val json = args.firstOrNull() as? JSONObject ?: return@on
                    val event = gson.fromJson(json.toString(), CallEndEvent::class.java)
                    _callEnded.tryEmit(event)
                }
                on("ice_candidate") { args ->
                    val json = args.firstOrNull() as? JSONObject ?: return@on
                    val event = gson.fromJson(json.toString(), IceCandidateEvent::class.java)
                    _remoteIceCandidate.tryEmit(event)
                }

                connect()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create socket: ${e.message}", e)
        }
    }

    /** Disconnects the socket and clears state. */
    fun disconnect() {
        socket?.disconnect()
        socket?.off()
        socket = null
        _isConnected.tryEmit(false)
        _currentRoom.tryEmit(null)
    }

    // ── Emit helpers ────────────────────────────────────────────────

    fun joinRoom(roomId: String) {
        val payload = JSONObject().apply { put("roomId", roomId) }
        emit("join_room", payload)
    }

    fun sendMessage(roomId: String, content: String) {
        val payload = JSONObject().apply {
            put("roomId", roomId)
            put("content", content)
        }
        emit("send_message", payload)
    }

    fun markRead(roomId: String) {
        val payload = JSONObject().apply { put("roomId", roomId) }
        emit("mark_read", payload)
    }

    fun sendTyping(roomId: String, isTyping: Boolean) {
        val payload = JSONObject().apply {
            put("roomId", roomId)
            put("isTyping", isTyping)
        }
        emit("typing", payload)
    }

    // ── WebRTC signaling emitters ────────────────────────────────────

    fun emitCallOffer(roomId: String, sdp: String) {
        val payload = JSONObject().apply {
            put("roomId", roomId)
            put("sdp", sdp)
        }
        emit("call_offer", payload)
    }

    fun emitCallAnswer(roomId: String, sdp: String) {
        val payload = JSONObject().apply {
            put("roomId", roomId)
            put("sdp", sdp)
        }
        emit("call_answer", payload)
    }

    fun emitCallReject(roomId: String) {
        val payload = JSONObject().apply { put("roomId", roomId) }
        emit("call_reject", payload)
    }

    fun emitCallEnd(roomId: String) {
        val payload = JSONObject().apply { put("roomId", roomId) }
        emit("call_end", payload)
    }

    fun emitIceCandidate(roomId: String, candidate: IceCandidateDto) {
        val candidateJson = JSONObject().apply {
            put("sdpMid", candidate.sdpMid)
            put("sdpMLineIndex", candidate.sdpMLineIndex)
            put("candidate", candidate.candidate)
        }
        val payload = JSONObject().apply {
            put("roomId", roomId)
            put("candidate", candidateJson)
        }
        emit("ice_candidate", payload)
    }

    // ── Private ─────────────────────────────────────────────────────

    private fun emit(event: String, payload: JSONObject) {
        if (socket?.connected() == true) {
            socket?.emit(event, payload)
        } else {
            Log.w(TAG, "emit($event) dropped — socket not connected")
        }
    }
}
