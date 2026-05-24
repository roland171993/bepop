package com.stopgalere.presentation.ui.chat

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.stopgalere.data.remote.dto.CallOfferEvent
import com.stopgalere.data.remote.dto.MessageDto
import com.stopgalere.navigation.Route
import java.io.File

// ─── Test tags ─────────────────────────────────────────────────────────────────
object ChatTestTags {
    const val SCREEN           = "chat_screen"
    const val MESSAGE_LIST     = "chat_message_list"
    const val MESSAGE_INPUT    = "chat_message_input"
    const val SEND_BUTTON      = "chat_send_button"
    const val ATTACH_BUTTON    = "chat_attach_button"
    const val TYPING_INDICATOR = "chat_typing_indicator"
    const val ERROR_SNACKBAR   = "chat_error_snackbar"
    const val LOADING          = "chat_loading"
    const val CONNECTION_BADGE = "chat_connection_badge"
    const val CALL_BUTTON      = "chat_call_btn"
}

// ─── Screen ────────────────────────────────────────────────────────────────────

/**
 * Chat screen entry point — fetches the ViewModel and derives the roomId from
 * the authenticated user's ID (pattern: "support-{userId}").
 */
@Composable
fun ChatScreen(
    navController: NavHostController,
    userId:        String,
    viewModel:     ChatViewModel = hiltViewModel()
) {
    val roomId = "support-$userId"

    // Connect & join once
    LaunchedEffect(roomId) {
        viewModel.enterRoom(roomId)
    }

    // Disconnect when leaving the screen
    DisposableEffect(Unit) {
        onDispose { viewModel.leaveRoom() }
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ChatScreenContent(
        state              = state,
        onSend             = { viewModel.sendMessage(it) },
        onTyping           = { viewModel.onTyping() },
        onAttach           = { file, mime -> viewModel.uploadFile(file, mime) },
        onMarkRead         = { viewModel.markRead() },
        onConsumeErr       = { viewModel.consumeError() },
        onBack             = { navController.popBackStack() },
        onStartCall        = { navController.navigate(Route.VideoCall.of(roomId)) },
        onAcceptIncomingCall = { offer ->
            viewModel.consumeIncomingCall()
            navController.navigate(Route.VideoCall.of(offer.roomId))
        },
        onRejectIncomingCall = {
            viewModel.consumeIncomingCall()
        }
    )
}

// ─── Stateless content (testable) ──────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreenContent(
    state:                ChatUiState,
    onSend:               (String) -> Unit,
    onTyping:             () -> Unit,
    onAttach:             (File, String) -> Unit,
    onMarkRead:           () -> Unit,
    onConsumeErr:         () -> Unit,
    onBack:               () -> Unit,
    onStartCall:          () -> Unit           = {},
    onAcceptIncomingCall: (CallOfferEvent) -> Unit = {},
    onRejectIncomingCall: () -> Unit           = {}
) {
    val context       = LocalContext.current
    val snackbarHost  = remember { SnackbarHostState() }
    val listState     = rememberLazyListState()
    val keyboard      = LocalSoftwareKeyboardController.current
    var inputText     by remember { mutableStateOf("") }

    // ── File picker ────────────────────────────────────────────────
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"
        val tmpFile  = File(context.cacheDir, "chat_${System.currentTimeMillis()}")
        context.contentResolver.openInputStream(uri)?.use { it.copyTo(tmpFile.outputStream()) }
        onAttach(tmpFile, mimeType)
    }

    // ── Scroll to bottom on new message ───────────────────────────
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    // ── Mark read when screen is in focus ─────────────────────────
    LaunchedEffect(state.messages.size, state.isConnected) {
        if (state.isConnected) onMarkRead()
    }

    // ── Error snackbar ────────────────────────────────────────────
    LaunchedEffect(state.errorMessage) {
        if (state.errorMessage != null) {
            snackbarHost.showSnackbar(
                message     = state.errorMessage,
                duration    = SnackbarDuration.Short,
                actionLabel = "OK"
            )
            onConsumeErr()
        }
    }

    Scaffold(
        modifier     = Modifier.testTag(ChatTestTags.SCREEN),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHost, modifier = Modifier.testTag(ChatTestTags.ERROR_SNACKBAR))
        },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Assistance", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.width(8.dp))
                        // Connection badge
                        val badgeColor = if (state.isConnected) Color(0xFF4CAF50) else Color(0xFFBDBDBD)
                        Box(
                            Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(badgeColor)
                                .testTag(ChatTestTags.CONNECTION_BADGE)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    IconButton(
                        onClick  = onStartCall,
                        modifier = Modifier.testTag(ChatTestTags.CALL_BUTTON)
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = "Appel vidéo")
                    }
                }
            )
        },
        bottomBar = {
            Column {
                // Typing indicator
                if (state.remoteIsTyping) {
                    Text(
                        text     = "Le conseiller est en train d'écrire…",
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .testTag(ChatTestTags.TYPING_INDICATOR),
                        style    = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Input bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick  = { filePicker.launch("*/*") },
                        modifier = Modifier.testTag(ChatTestTags.ATTACH_BUTTON)
                    ) {
                        Icon(Icons.Default.AttachFile, contentDescription = "Joindre un fichier")
                    }

                    OutlinedTextField(
                        value         = inputText,
                        onValueChange = {
                            inputText = it
                            if (it.isNotBlank()) onTyping()
                        },
                        modifier      = Modifier
                            .weight(1f)
                            .testTag(ChatTestTags.MESSAGE_INPUT),
                        placeholder   = { Text("Votre message…") },
                        singleLine    = false,
                        maxLines      = 4,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (inputText.isNotBlank()) {
                                    onSend(inputText.trim())
                                    inputText = ""
                                    keyboard?.hide()
                                }
                            }
                        )
                    )

                    Spacer(Modifier.width(4.dp))

                    IconButton(
                        onClick  = {
                            if (inputText.isNotBlank()) {
                                onSend(inputText.trim())
                                inputText = ""
                                keyboard?.hide()
                            }
                        },
                        enabled  = inputText.isNotBlank() && !state.isSending,
                        modifier = Modifier.testTag(ChatTestTags.SEND_BUTTON)
                    ) {
                        if (state.isSending) {
                            CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Envoyer")
                        }
                    }
                }
            }
        }
    ) { padding ->
        // ── Incoming call dialog ─────────────────────────────────────
        state.incomingCall?.let { offer ->
            AlertDialog(
                onDismissRequest = onRejectIncomingCall,
                title   = { Text("Appel entrant") },
                text    = { Text("Un appel vidéo entrant. Voulez-vous accepter ?") },
                confirmButton = {
                    TextButton(onClick = { onAcceptIncomingCall(offer) }) {
                        Text("Accepter")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onRejectIncomingCall) {
                        Text("Refuser")
                    }
                }
            )
        }

        if (state.isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .testTag(ChatTestTags.LOADING),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                state          = listState,
                contentPadding = PaddingValues(
                    start  = 16.dp,
                    end    = 16.dp,
                    top    = padding.calculateTopPadding() + 8.dp,
                    bottom = padding.calculateBottomPadding() + 8.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier            = Modifier.testTag(ChatTestTags.MESSAGE_LIST)
            ) {
                items(state.messages, key = { it.id }) { message ->
                    MessageBubble(message = message)
                }
            }
        }
    }
}

// ─── Message bubble ────────────────────────────────────────────────────────────

@Composable
private fun MessageBubble(message: MessageDto) {
    val isUser        = message.senderType == "user"
    val bubbleColor   = if (isUser) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
    val textColor     = if (isUser) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant
    val alignment     = if (isUser) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart    = if (isUser) 12.dp else 2.dp,
                        topEnd      = if (isUser) 2.dp  else 12.dp,
                        bottomStart = 12.dp,
                        bottomEnd   = 12.dp
                    )
                )
                .background(bubbleColor)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Column {
                if (!message.content.isNullOrBlank()) {
                    Text(
                        text  = message.content,
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (message.fileUrl != null) {
                    val icon = if (message.fileType == "image") "🖼️" else "📄"
                    Text(
                        text  = "$icon ${message.fileName ?: "Fichier"}",
                        color = textColor,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = if (message.content.isNullOrBlank()) 0.dp else 4.dp)
                    )
                }
                Text(
                    text  = message.createdAt.take(16).replace("T", " "),
                    color = textColor.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.End).padding(top = 2.dp)
                )
            }
        }
    }
}
