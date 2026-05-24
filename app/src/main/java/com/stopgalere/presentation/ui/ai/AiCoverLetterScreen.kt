package com.stopgalere.presentation.ui.ai

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.stopgalere.data.remote.dto.AiProvider
import com.stopgalere.presentation.viewmodel.AiCoverLetterViewModel
import java.io.File
import java.util.Locale

object AiCoverLetterTestTags {
    const val SCREEN = "ai_cover_letter_screen"
    const val ADD_DOCS_BTN = "add_docs_button"
    const val OCR_BTN = "ocr_button"
    const val RECORD_BTN = "record_button"
    const val PROVIDER_SELECTOR = "provider_selector"
    const val GENERATE_BTN = "generate_button"
    const val RESULT_TEXT = "result_text"
    const val SPEAK_BTN = "speak_button"
    const val EMAIL_FIELD = "email_field"
    const val LOADING = "loading_indicator"
    const val ERROR_MSG = "error_message"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiCoverLetterScreen(
    navController: NavHostController,
    jobId: String,
    jobTitle: String,
    jobDescription: String,
    jobCompany: String,
    viewModel: AiCoverLetterViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Galerie multi-sélection
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris.isNotEmpty()) viewModel.addImageUris(uris)
    }

    // Caméra
    val cameraUri = remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) cameraUri.value?.let { viewModel.addImageUris(listOf(it)) }
    }

    // Speech-to-Text
    val speechLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val results = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            viewModel.updateVoiceTranscript(results?.firstOrNull() ?: "")
        }
    }

    // Snackbar erreur
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("✨ Lettre IA") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .testTag(AiCoverLetterTestTags.SCREEN),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Section Documents ──────────────────────────────────────
            item {
                Text("📄 Documents (diplômes, certificats...)", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.selectedImageUris) { uri ->
                        Box {
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp)
                            )
                            IconButton(
                                onClick = { viewModel.removeImageUri(uri) },
                                modifier = Modifier.align(Alignment.TopEnd).size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Supprimer", tint = Color.Red)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.testTag(AiCoverLetterTestTags.ADD_DOCS_BTN)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Galerie")
                    }
                    OutlinedButton(onClick = {
                        val file = File(context.cacheDir, "cam_${System.currentTimeMillis()}.jpg")
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                        cameraUri.value = uri
                        cameraLauncher.launch(uri)
                    }) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Caméra")
                    }
                }
            }

            // ── Bouton OCR ─────────────────────────────────────────────
            if (state.selectedImageUris.isNotEmpty()) {
                item {
                    Button(
                        onClick = { viewModel.runOcr() },
                        enabled = !state.isOcrRunning,
                        modifier = Modifier.testTag(AiCoverLetterTestTags.OCR_BTN)
                    ) {
                        if (state.isOcrRunning) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                            Spacer(Modifier.width(8.dp))
                        }
                        Text("🔍 Analyser les documents")
                    }
                    if (state.ocrTexts.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text("✅ ${state.ocrTexts.size} document(s) analysé(s)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // ── Section Voix ───────────────────────────────────────────
            item {
                Text("🎙️ Prompt vocal (optionnel)", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FilledIconButton(
                        onClick = {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                                putExtra(RecognizerIntent.EXTRA_PROMPT, "Décrivez votre parcours ou vos motivations...")
                            }
                            viewModel.setRecording(true)
                            speechLauncher.launch(intent)
                        },
                        modifier = Modifier.testTag(AiCoverLetterTestTags.RECORD_BTN),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (state.isRecording) Color.Red else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = "Enregistrer")
                    }
                    Text(if (state.isRecording) "Écoute..." else "Appuyez pour parler", style = MaterialTheme.typography.bodyMedium)
                }
                if (state.voiceTranscript.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.voiceTranscript,
                        onValueChange = { viewModel.updateVoiceTranscript(it) },
                        label = { Text("Transcription") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            }

            // ── Provider ───────────────────────────────────────────────
            item {
                Text("🤖 Modèle IA", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.testTag(AiCoverLetterTestTags.PROVIDER_SELECTOR)
                ) {
                    AiProvider.entries.forEach { provider ->
                        FilterChip(
                            selected = state.selectedProvider == provider,
                            onClick = { viewModel.selectProvider(provider) },
                            label = { Text(provider.label) }
                        )
                    }
                }
            }

            // ── Email ──────────────────────────────────────────────────
            item {
                OutlinedTextField(
                    value = state.sendToEmail,
                    onValueChange = { viewModel.updateSendToEmail(it) },
                    label = { Text("📧 Envoyer par email (optionnel)") },
                    modifier = Modifier.fillMaxWidth().testTag(AiCoverLetterTestTags.EMAIL_FIELD),
                    singleLine = true
                )
            }

            // ── Bouton Générer ─────────────────────────────────────────
            item {
                Button(
                    onClick = { viewModel.generate(jobTitle, jobDescription, jobCompany.ifBlank { null }) },
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth().testTag(AiCoverLetterTestTags.GENERATE_BTN)
                ) {
                    Text("✨ Générer la lettre de motivation")
                }
            }

            // ── Loading ────────────────────────────────────────────────
            if (state.isLoading) {
                item {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.testTag(AiCoverLetterTestTags.LOADING))
                    }
                }
            }

            // ── Résultat ───────────────────────────────────────────────
            if (state.generatedText.isNotBlank()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("📝 Lettre générée", style = MaterialTheme.typography.titleMedium)
                                Row {
                                    IconButton(
                                        onClick = { viewModel.speakOrStop() },
                                        modifier = Modifier.testTag(AiCoverLetterTestTags.SPEAK_BTN)
                                    ) {
                                        Icon(
                                            if (state.isSpeaking) Icons.Default.StopCircle else Icons.Default.VolumeUp,
                                            contentDescription = if (state.isSpeaking) "Stop" else "Écouter"
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            SelectionContainer {
                                Text(
                                    text = state.generatedText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.testTag(AiCoverLetterTestTags.RESULT_TEXT)
                                )
                            }
                            if (state.emailSent) {
                                Spacer(Modifier.height(8.dp))
                                Text("✅ Email envoyé à ${state.sendToEmail}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}
