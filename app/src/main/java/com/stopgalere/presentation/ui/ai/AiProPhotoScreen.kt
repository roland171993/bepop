package com.stopgalere.presentation.ui.ai

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.stopgalere.data.remote.dto.AiProvider
import com.stopgalere.presentation.viewmodel.AiProPhotoViewModel
import com.stopgalere.util.AppConstants
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.Locale

object AiProPhotoTestTags {
    const val SCREEN = "ai_pro_photo_screen"
    const val PICK_PHOTO_BTN = "pick_photo_button"
    const val CROP_BTN = "crop_button"
    const val WITH_SUIT_TOGGLE = "with_suit_toggle"
    const val BACKGROUND_FIELD = "background_field"
    const val RECORD_BTN = "record_button"
    const val PROVIDER_SELECTOR = "provider_selector"
    const val GENERATE_BTN = "generate_button"
    const val RESULT_IMAGE = "result_image"
    const val EMAIL_FIELD = "email_field"
    const val LOADING = "loading_indicator"
    const val ERROR_MSG = "error_message"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiProPhotoScreen(
    navController: NavHostController,
    jobId: String,
    jobTitle: String,
    viewModel: AiProPhotoViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Galerie
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.setSelectedImage(it) }
    }

    // Caméra
    val cameraUri = remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) cameraUri.value?.let { viewModel.setSelectedImage(it) }
    }

    // uCrop
    val uCropLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { UCrop.getOutput(it)?.let { uri -> viewModel.setCroppedImage(uri) } }
        }
    }

    // Speech-to-Text
    val speechLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val results = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            viewModel.updateVoiceDescription(results?.firstOrNull() ?: "")
        }
    }

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
                title = { Text("📸 Photo Professionnelle IA") },
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
                .testTag(AiProPhotoTestTags.SCREEN),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Section Photo ──────────────────────────────────────────
            item {
                Text("🖼️ Votre photo", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                val displayUri = state.croppedImageUri ?: state.selectedImageUri
                Card(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (displayUri != null) {
                            AsyncImage(
                                model = displayUri,
                                contentDescription = "Photo sélectionnée",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                                Text("Aucune photo sélectionnée", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.testTag(AiProPhotoTestTags.PICK_PHOTO_BTN)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
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
                    if (state.selectedImageUri != null) {
                        OutlinedButton(
                            onClick = {
                                val src = state.selectedImageUri!!
                                val dest = Uri.fromFile(File(context.cacheDir, "cropped_${System.currentTimeMillis()}.jpg"))
                                val intent = UCrop.of(src, dest)
                                    .withAspectRatio(1f, 1f)
                                    .withMaxResultSize(512, 512)
                                    .getIntent(context)
                                uCropLauncher.launch(intent)
                            },
                            modifier = Modifier.testTag(AiProPhotoTestTags.CROP_BTN)
                        ) {
                            Icon(Icons.Default.Crop, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Recadrer")
                        }
                    }
                }
            }

            // ── Style ──────────────────────────────────────────────────
            item {
                Text("👔 Style vestimentaire", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.testTag(AiProPhotoTestTags.WITH_SUIT_TOGGLE)
                ) {
                    FilterChip(
                        selected = state.withSuit,
                        onClick = { viewModel.setWithSuit(true) },
                        label = { Text("Avec costume") }
                    )
                    FilterChip(
                        selected = !state.withSuit,
                        onClick = { viewModel.setWithSuit(false) },
                        label = { Text("Sans costume") }
                    )
                }
            }

            // ── Fond ───────────────────────────────────────────────────
            item {
                OutlinedTextField(
                    value = state.backgroundStyle,
                    onValueChange = { viewModel.setBackgroundStyle(it) },
                    label = { Text("🏢 Style de fond") },
                    placeholder = { Text("ex: bureau professionnel, blanc neutre, extérieur...") },
                    modifier = Modifier.fillMaxWidth().testTag(AiProPhotoTestTags.BACKGROUND_FIELD),
                    singleLine = true
                )
            }

            // ── Voix ───────────────────────────────────────────────────
            item {
                Text("🎙️ Description vocale (optionnel)", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FilledIconButton(
                        onClick = {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                                putExtra(RecognizerIntent.EXTRA_PROMPT, "Décrivez le look souhaité...")
                            }
                            viewModel.setRecording(true)
                            speechLauncher.launch(intent)
                        },
                        modifier = Modifier.testTag(AiProPhotoTestTags.RECORD_BTN),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (state.isRecording) Color.Red else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = "Enregistrer")
                    }
                    Text(if (state.isRecording) "Écoute..." else "Appuyez pour parler", style = MaterialTheme.typography.bodyMedium)
                }
                if (state.voiceDescription.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.voiceDescription,
                        onValueChange = { viewModel.updateVoiceDescription(it) },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            }

            // ── Provider ───────────────────────────────────────────────
            item {
                Text("🤖 Modèle IA", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text("Note : la génération d'image utilise DALL·E (OpenAI)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.testTag(AiProPhotoTestTags.PROVIDER_SELECTOR)
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
                    modifier = Modifier.fillMaxWidth().testTag(AiProPhotoTestTags.EMAIL_FIELD),
                    singleLine = true
                )
            }

            // ── Bouton Générer ─────────────────────────────────────────
            item {
                Button(
                    onClick = { viewModel.generate(jobTitle) },
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth().testTag(AiProPhotoTestTags.GENERATE_BTN)
                ) {
                    Text("✨ Générer la photo professionnelle")
                }
            }

            // ── Loading ────────────────────────────────────────────────
            if (state.isLoading) {
                item {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.testTag(AiProPhotoTestTags.LOADING))
                    }
                }
            }

            // ── Résultat ───────────────────────────────────────────────
            if (state.generatedPhotoUrl.isNotBlank()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("📸 Photo générée", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            // BASE_URL ends with "/api/" — strip that suffix to get the server root
                            val serverRoot = AppConstants.BASE_URL.removeSuffix("/").let {
                                if (it.endsWith("/api")) it.removeSuffix("/api") else it
                            }
                            val fullUrl = serverRoot + state.generatedPhotoUrl
                            AsyncImage(
                                model = fullUrl,
                                contentDescription = "Photo professionnelle générée",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .testTag(AiProPhotoTestTags.RESULT_IMAGE)
                            )
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
