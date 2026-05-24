package com.stopgalere.presentation.ui.call

/**
 * Represents the lifecycle state of a video call.
 */
enum class CallState {
    /** No active call. */
    IDLE,
    /** Outgoing call in progress — waiting for the remote peer to answer. */
    CALLING,
    /** Incoming call — waiting for the local user to accept or reject. */
    INCOMING,
    /** Both peers are connected; media is flowing. */
    CONNECTED,
    /** Call has ended (either side hung up or the call was rejected). */
    ENDED
}

/**
 * Immutable UI state for the video call screen.
 *
 * @param callState     Current phase of the call lifecycle.
 * @param roomId        Room ID for the call session, or null when idle.
 * @param isMuted       True when the local microphone is muted.
 * @param isCameraOff   True when the local camera is disabled.
 * @param isFrontCamera True when the front camera is active.
 * @param errorMessage  Non-null when an error should be shown.
 */
data class CallUiState(
    val callState:    CallState = CallState.IDLE,
    val roomId:       String?   = null,
    val isMuted:      Boolean   = false,
    val isCameraOff:  Boolean   = false,
    val isFrontCamera: Boolean  = true,
    val errorMessage: String?   = null
)
