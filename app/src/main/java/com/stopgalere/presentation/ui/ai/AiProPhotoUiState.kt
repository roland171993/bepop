package com.stopgalere.presentation.ui.ai

import android.net.Uri
import com.stopgalere.data.remote.dto.AiProvider

data class AiProPhotoUiState(
    val isLoading: Boolean = false,
    val selectedImageUri: Uri? = null,
    val croppedImageUri: Uri? = null,
    val withSuit: Boolean = true,
    val backgroundStyle: String = "professional office blur",
    val voiceDescription: String = "",
    val isRecording: Boolean = false,
    val selectedProvider: AiProvider = AiProvider.OPENAI,
    val generatedPhotoUrl: String = "",
    val sendToEmail: String = "",
    val emailSent: Boolean = false,
    val error: String? = null
)
