package com.stopgalere.data.remote.dto

import com.google.gson.annotations.SerializedName

// ─── WebRTC signaling payloads ────────────────────────────────────────────────

/**
 * Received when another peer initiates a call (server → client: call_incoming).
 */
data class CallOfferEvent(
    @SerializedName("roomId")     val roomId:     String,
    @SerializedName("fromUserId") val fromUserId: String,
    @SerializedName("sdp")        val sdp:        String
)

/**
 * Received when the remote peer accepts the call (server → client: call_answered).
 */
data class CallAnswerEvent(
    @SerializedName("roomId") val roomId: String,
    @SerializedName("sdp")    val sdp:    String
)

/**
 * Received when the remote peer rejects the call (server → client: call_rejected).
 */
data class CallRejectEvent(
    @SerializedName("roomId") val roomId: String
)

/**
 * Received when either party ends the call (server → client: call_ended).
 */
data class CallEndEvent(
    @SerializedName("roomId") val roomId: String
)

/**
 * An ICE candidate exchanged during WebRTC negotiation.
 */
data class IceCandidateDto(
    @SerializedName("sdpMid")        val sdpMid:        String?,
    @SerializedName("sdpMLineIndex") val sdpMLineIndex: Int,
    @SerializedName("candidate")     val candidate:     String
)

/**
 * Wrapper for an ICE candidate event (server → client: ice_candidate).
 */
data class IceCandidateEvent(
    @SerializedName("roomId")    val roomId:    String,
    @SerializedName("candidate") val candidate: IceCandidateDto
)
