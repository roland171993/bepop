package com.stopgalere.presentation.ui.ai

import android.net.Uri
import com.stopgalere.data.remote.dto.AiProvider

data class AiCoverLetterUiState(
    val isLoading: Boolean = false,
    val selectedImageUris: List<Uri> = emptyList(),
    val isOcrRunning: Boolean = false,
    val ocrTexts: List<String> = emptyList(),
    val voiceTranscript: String = "",
    val isRecording: Boolean = false,
    val selectedProvider: AiProvider = AiProvider.OPENAI,
    val generatedText: String = "",
    val isSpeaking: Boolean = false,
    val sendToEmail: String = "",
    val emailSent: Boolean = false,
    val error: String? = null
)
