package com.stopgalere.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GenerateCoverLetterRequest(
    @SerializedName("jobTitle") val jobTitle: String,
    @SerializedName("jobDescription") val jobDescription: String,
    @SerializedName("jobCompany") val jobCompany: String? = null,
    @SerializedName("ocrTexts") val ocrTexts: List<String> = emptyList(),
    @SerializedName("voiceTranscript") val voiceTranscript: String = "",
    @SerializedName("provider") val provider: String = "openai",
    @SerializedName("sendToEmail") val sendToEmail: String? = null
)

data class GenerateCoverLetterResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("coverLetter") val coverLetter: String
)

data class GenerateProPhotoResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("photoUrl") val photoUrl: String
)

enum class AiProvider(val value: String, val label: String) {
    OPENAI("openai", "ChatGPT"),
    GEMINI("gemini", "Gemini"),
    DEEPSEEK("deepseek", "DeepSeek")
}
