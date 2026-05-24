package com.stopgalere.presentation.viewmodel

import android.content.Context
import android.net.Uri
import android.speech.tts.TextToSpeech
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.stopgalere.data.remote.dto.AiProvider
import com.stopgalere.data.remote.dto.GenerateCoverLetterRequest
import com.stopgalere.domain.usecase.ai.GenerateCoverLetterUseCase
import com.stopgalere.presentation.ui.ai.AiCoverLetterUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class AiCoverLetterViewModel @Inject constructor(
    private val generateCoverLetterUseCase: GenerateCoverLetterUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiCoverLetterUiState())
    val uiState = _uiState.asStateFlow()

    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.FRENCH
            }
        }
    }

    fun addImageUris(uris: List<Uri>) {
        _uiState.update { state ->
            val existing = state.selectedImageUris.toSet()
            state.copy(selectedImageUris = state.selectedImageUris + uris.filter { it !in existing })
        }
    }

    fun removeImageUri(uri: Uri) {
        _uiState.update { it.copy(selectedImageUris = it.selectedImageUris.filter { u -> u != uri }) }
    }

    fun runOcr() {
        val uris = _uiState.value.selectedImageUris
        if (uris.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isOcrRunning = true, ocrTexts = emptyList()) }
            val results = uris.map { uri -> runOcrOnUri(uri) }.filter { it.isNotBlank() }
            _uiState.update { it.copy(isOcrRunning = false, ocrTexts = results) }
        }
    }

    private suspend fun runOcrOnUri(uri: Uri): String = suspendCancellableCoroutine { cont ->
        try {
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val image = InputImage.fromFilePath(context, uri)
            recognizer.process(image)
                .addOnSuccessListener { result -> cont.resume(result.text) }
                .addOnFailureListener { cont.resume("") }
        } catch (e: Exception) {
            cont.resume("")
        }
    }

    fun updateVoiceTranscript(text: String) {
        _uiState.update { it.copy(voiceTranscript = text, isRecording = false) }
    }

    fun setRecording(isRecording: Boolean) {
        _uiState.update { it.copy(isRecording = isRecording) }
    }

    fun selectProvider(provider: AiProvider) {
        _uiState.update { it.copy(selectedProvider = provider) }
    }

    fun updateSendToEmail(email: String) {
        _uiState.update { it.copy(sendToEmail = email) }
    }

    fun generate(jobTitle: String, jobDescription: String, jobCompany: String?) {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, generatedText = "") }
            val request = GenerateCoverLetterRequest(
                jobTitle = jobTitle,
                jobDescription = jobDescription,
                jobCompany = jobCompany,
                ocrTexts = state.ocrTexts,
                voiceTranscript = state.voiceTranscript,
                provider = state.selectedProvider.value,
                sendToEmail = state.sendToEmail.ifBlank { null }
            )
            generateCoverLetterUseCase(request)
                .onSuccess { text ->
                    _uiState.update { it.copy(isLoading = false, generatedText = text, emailSent = it.sendToEmail.isNotBlank()) }
                }
                .onFailure { e ->
                    FirebaseCrashlytics.getInstance().recordException(e)
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Erreur inconnue") }
                }
        }
    }

    fun speakOrStop() {
        val state = _uiState.value
        if (state.isSpeaking) {
            tts?.stop()
            _uiState.update { it.copy(isSpeaking = false) }
        } else {
            tts?.speak(state.generatedText, TextToSpeech.QUEUE_FLUSH, null, "cl_tts")
            _uiState.update { it.copy(isSpeaking = true) }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        tts?.shutdown()
    }
}
