package com.stopgalere.data.remote.dto

import com.google.gson.annotations.SerializedName

// ─── Domain models ────────────────────────────────────────────────────────────

/**
 * Mirrors the backend Message document.
 *
 * senderType: "user" | "support"
 * fileType:   "image" | "document" | null
 */
data class MessageDto(
    @SerializedName("_id")         val id:         String,
    @SerializedName("roomId")      val roomId:     String,
    @SerializedName("senderId")    val senderId:   String?,
    @SerializedName("senderType")  val senderType: String,   // "user" | "support"
    @SerializedName("content")     val content:    String,
    @SerializedName("fileUrl")     val fileUrl:    String?,
    @SerializedName("fileName")    val fileName:   String?,
    @SerializedName("fileSize")    val fileSize:   Long?,
    @SerializedName("fileType")    val fileType:   String?,  // "image" | "document" | null
    @SerializedName("readAt")      val readAt:     String?,
    @SerializedName("createdAt")   val createdAt:  String
)

/** Mirrors the backend Room document. */
data class RoomDto(
    @SerializedName("_id")             val id:              String,
    @SerializedName("userId")          val userId:          String,
    @SerializedName("roomId")          val roomId:          String,
    @SerializedName("status")          val status:          String,   // "open" | "closed"
    @SerializedName("lastMessage")     val lastMessage:     String,
    @SerializedName("lastMessageAt")   val lastMessageAt:   String?,
    @SerializedName("unreadBySupport") val unreadBySupport: Int,
    @SerializedName("unreadByUser")    val unreadByUser:    Int
)

// ─── REST Response wrappers ───────────────────────────────────────────────────

data class RoomsResponse(
    @SerializedName("rooms") val rooms: List<RoomDto>
)

data class MessagesResponse(
    @SerializedName("messages") val messages: List<MessageDto>
)

data class UploadFileResponse(
    @SerializedName("message") val message: MessageDto
)

// ─── Socket.io event payloads (serialised manually via Gson) ─────────────────

/** Payload for "new_message" events received from the server. */
data class NewMessageEvent(
    @SerializedName("message") val message: MessageDto
)

/** Payload for "room_joined" events. */
data class RoomJoinedEvent(
    @SerializedName("room") val room: RoomDto
)

/** Payload for "messages_read" events. */
data class MessagesReadEvent(
    @SerializedName("roomId") val roomId: String,
    @SerializedName("readAt") val readAt: String
)

/** Payload for "user_typing" events. */
data class UserTypingEvent(
    @SerializedName("roomId")   val roomId:   String,
    @SerializedName("isTyping") val isTyping: Boolean,
    @SerializedName("sender")   val sender:   String   // "user" | "support"
)
