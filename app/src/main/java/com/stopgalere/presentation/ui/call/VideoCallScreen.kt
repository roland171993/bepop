package com.stopgalere.presentation.ui.call

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.stopgalere.data.remote.dto.CallOfferEvent
import org.webrtc.SurfaceViewRenderer

// ─── Test tags ────────────────────────────────────────────────────────────────

object CallTestTags {
    const val VIDEO_REMOTE    = "video_remote"
    const val VIDEO_LOCAL     = "video_local"
    const val CALL_ACCEPT_BTN = "call_accept_btn"
    const val CALL_REJECT_BTN = "call_reject_btn"
    const val CALL_END_BTN    = "call_end_btn"
    const val CALL_MUTE_BTN   = "call_mute_btn"
    const val CALL_CAMERA_BTN = "call_camera_btn"
    const val CALL_SWITCH_BTN = "call_switch_btn"
    const val CALL_LOADING    = "call_loading"
}

// ─── Entry point composable ────────────────────────────────────────────────────

/**
 * VideoCallScreen
 *
 * Entry point composable that wires the ViewModel and navigation lifecycle.
 * When navigated to with a [roomId] (outgoing call), [CallViewModel.startCall] is triggered.
 * Cleans up with [CallViewModel.endCall] on disposal.
 *
 * @param navController Navigation controller for back-stack management.
 * @param roomId        The room ID of the call. Null means waiting for an incoming call.
 */
@Composable
fun VideoCallScreen(
    navController: NavHostController,
    roomId:        String?,
    viewModel:     com.stopgalere.presentation.viewmodel.CallViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // SurfaceViewRenderer instances created once
    val localRenderer  = remember { SurfaceViewRenderer(navController.context) }
    val remoteRenderer = remember { SurfaceViewRenderer(navController.context) }

    // Initiate outgoing call when roomId is provided
    LaunchedEffect(roomId) {
        if (!roomId.isNullOrBlank()) {
            viewModel.startCall(roomId)
        }
    }

    // Navigate back when the call has ended
    LaunchedEffect(state.callState) {
        if (state.callState == CallState.ENDED) {
            navController.popBackStack()
        }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.endCall() }
    }

    VideoCallScreenContent(
        state           = state,
        onAccept        = { offer -> viewModel.acceptCall(offer) },
        onReject        = { id -> viewModel.rejectCall(id) },
        onEnd           = { viewModel.endCall() },
        onToggleMic     = { viewModel.toggleMic() },
        onToggleCamera  = { viewModel.toggleCamera() },
        onSwitchCamera  = { viewModel.switchCamera() },
        localRenderer   = localRenderer,
        remoteRenderer  = remoteRenderer
    )
}

// ─── Stateless content (testable) ─────────────────────────────────────────────

/**
 * Stateless composable for the video call UI.
 *
 * Layout:
 * - Full-screen remote video
 * - Small PiP local video at bottom-right
 * - INCOMING: overlay with Accept / Reject buttons
 * - CALLING / CONNECTED: bottom control bar
 * - CALLING (not yet CONNECTED): loading spinner overlay
 */
@Composable
fun VideoCallScreenContent(
    state:          CallUiState,
    onAccept:       (CallOfferEvent) -> Unit,
    onReject:       (String) -> Unit,
    onEnd:          () -> Unit,
    onToggleMic:    () -> Unit,
    onToggleCamera: () -> Unit,
    onSwitchCamera: () -> Unit,
    localRenderer:  SurfaceViewRenderer,
    remoteRenderer: SurfaceViewRenderer
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // ── Full-screen remote video ────────────────────────────────
        AndroidView(
            factory  = { remoteRenderer },
            modifier = Modifier
                .fillMaxSize()
                .testTag(CallTestTags.VIDEO_REMOTE)
        )

        // ── PiP local video (bottom-right) ──────────────────────────
        AndroidView(
            factory  = { localRenderer },
            modifier = Modifier
                .size(width = 120.dp, height = 160.dp)
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag(CallTestTags.VIDEO_LOCAL)
        )

        // ── CALLING spinner overlay ─────────────────────────────────
        if (state.callState == CallState.CALLING) {
            Box(
                modifier            = Modifier.fillMaxSize(),
                contentAlignment    = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color    = Color.White,
                    modifier = Modifier.testTag(CallTestTags.CALL_LOADING)
                )
            }
        }

        // ── INCOMING overlay ────────────────────────────────────────
        if (state.callState == CallState.INCOMING) {
            IncomingCallOverlay(
                roomId   = state.roomId ?: "",
                onAccept = onAccept,
                onReject = onReject
            )
        }

        // ── CALLING / CONNECTED control bar ────────────────────────
        if (state.callState == CallState.CALLING || state.callState == CallState.CONNECTED) {
            CallControlBar(
                isMuted        = state.isMuted,
                isCameraOff    = state.isCameraOff,
                onEnd          = onEnd,
                onToggleMic    = onToggleMic,
                onToggleCamera = onToggleCamera,
                onSwitchCamera = onSwitchCamera,
                modifier       = Modifier.align(Alignment.BottomCenter)
            )
        }

        // ── Error snackbar ──────────────────────────────────────────
        state.errorMessage?.let { msg ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) { Text(msg, color = Color.White) }
        }
    }
}

// ─── Incoming call overlay ─────────────────────────────────────────────────────

@Composable
private fun IncomingCallOverlay(
    roomId:   String,
    onAccept: (CallOfferEvent) -> Unit,
    onReject: (String) -> Unit
) {
    Box(
        modifier         = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text  = "Appel entrant",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            Spacer(Modifier.height(32.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(48.dp)) {
                // Accept
                IconButton(
                    onClick  = {
                        onAccept(
                            CallOfferEvent(
                                roomId     = roomId,
                                fromUserId = "",
                                sdp        = ""
                            )
                        )
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color(0xFF4CAF50), CircleShape)
                        .testTag(CallTestTags.CALL_ACCEPT_BTN)
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Accepter", tint = Color.White)
                }
                // Reject
                IconButton(
                    onClick  = { onReject(roomId) },
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color(0xFFF44336), CircleShape)
                        .testTag(CallTestTags.CALL_REJECT_BTN)
                ) {
                    Icon(Icons.Default.CallEnd, contentDescription = "Refuser", tint = Color.White)
                }
            }
        }
    }
}

// ─── Control bar ──────────────────────────────────────────────────────────────

@Composable
private fun CallControlBar(
    isMuted:        Boolean,
    isCameraOff:    Boolean,
    onEnd:          () -> Unit,
    onToggleMic:    () -> Unit,
    onToggleCamera: () -> Unit,
    onSwitchCamera: () -> Unit,
    modifier:       Modifier = Modifier
) {
    Row(
        modifier            = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        // Mute / Unmute
        IconButton(
            onClick  = onToggleMic,
            modifier = Modifier.testTag(CallTestTags.CALL_MUTE_BTN)
        ) {
            Icon(
                imageVector  = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                contentDescription = if (isMuted) "Activer le micro" else "Couper le micro",
                tint         = Color.White
            )
        }

        // Camera on/off
        IconButton(
            onClick  = onToggleCamera,
            modifier = Modifier.testTag(CallTestTags.CALL_CAMERA_BTN)
        ) {
            Icon(
                imageVector  = if (isCameraOff) Icons.Default.VideocamOff else Icons.Default.Videocam,
                contentDescription = if (isCameraOff) "Activer la caméra" else "Désactiver la caméra",
                tint         = Color.White
            )
        }

        // End call
        IconButton(
            onClick  = onEnd,
            modifier = Modifier
                .size(56.dp)
                .background(Color(0xFFF44336), CircleShape)
                .testTag(CallTestTags.CALL_END_BTN)
        ) {
            Icon(Icons.Default.CallEnd, contentDescription = "Raccrocher", tint = Color.White)
        }

        // Switch camera
        IconButton(
            onClick  = onSwitchCamera,
            modifier = Modifier.testTag(CallTestTags.CALL_SWITCH_BTN)
        ) {
            Icon(Icons.Default.FlipCameraAndroid, contentDescription = "Changer de caméra", tint = Color.White)
        }
    }
}
