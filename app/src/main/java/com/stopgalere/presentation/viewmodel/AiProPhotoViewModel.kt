package com.stopgalere.presentation.viewmodel

import android.content.Context
import android.net.Uri
import android.speech.tts.TextToSpeech
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.stopgalere.data.remote.dto.AiProvider
import com.stopgalere.domain.usecase.ai.GenerateProPhotoUseCase
import com.stopgalere.presentation.ui.ai.AiProPhotoUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AiProPhotoViewModel @Inject constructor(
    private val generateProPhotoUseCase: GenerateProPhotoUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiProPhotoUiState())
    val uiState = _uiState.asStateFlow()

    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) tts?.language = Locale.FRENCH
        }
    }

    fun setSelectedImage(uri: Uri) { _uiState.update { it.copy(selectedImageUri = uri, croppedImageUri = null) } }
    fun setCroppedImage(uri: Uri) { _uiState.update { it.copy(croppedImageUri = uri) } }
    fun setWithSuit(value: Boolean) { _uiState.update { it.copy(withSuit = value) } }
    fun setBackgroundStyle(style: String) { _uiState.update { it.copy(backgroundStyle = style) } }
    fun updateVoiceDescription(text: String) { _uiState.update { it.copy(voiceDescription = text, isRecording = false) } }
    fun setRecording(value: Boolean) { _uiState.update { it.copy(isRecording = value) } }
    fun selectProvider(provider: AiProvider) { _uiState.update { it.copy(selectedProvider = provider) } }
    fun updateSendToEmail(email: String) { _uiState.update { it.copy(sendToEmail = email) } }

    fun generate(jobTitle: String) {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, generatedPhotoUrl = "") }
            generateProPhotoUseCase(
                photoUri = state.croppedImageUri ?: state.selectedImageUri,
                style = state.backgroundStyle,
                voiceDescription = state.voiceDescription,
                withSuit = state.withSuit,
                provider = state.selectedProvider.value,
                jobTitle = jobTitle,
                sendToEmail = state.sendToEmail.ifBlank { null }
            ).onSuccess { url ->
                _uiState.update { it.copy(isLoading = false, generatedPhotoUrl = url, emailSent = it.sendToEmail.isNotBlank()) }
                tts?.speak("Votre photo professionnelle a été générée avec succès.", TextToSpeech.QUEUE_FLUSH, null, "photo_done")
            }.onFailure { e ->
                FirebaseCrashlytics.getInstance().recordException(e)
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Erreur inconnue") }
            }
        }
    }

    fun clearError() { _uiState.update { it.copy(error = null) } }

    override fun onCleared() {
        super.onCleared()
        tts?.shutdown()
    }
}
