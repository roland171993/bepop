package com.stopgalere.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.stopgalere.data.remote.dto.CallOfferEvent
import com.stopgalere.data.remote.socket.SocketManager
import com.stopgalere.data.remote.webrtc.WebRtcManager
import com.stopgalere.presentation.ui.call.CallState
import com.stopgalere.presentation.ui.call.CallUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the video call screen.
 *
 * Drives the WebRTC lifecycle in response to both user actions and remote
 * signaling events received via [SocketManager].
 *
 * Memory-leak note: no Activity/Context held; only singleton DI dependencies.
 */
@HiltViewModel
class CallViewModel @Inject constructor(
    private val socketManager: SocketManager,
    private val webRtcManager: WebRtcManager
) : ViewModel() {

    companion object {
        private const val TAG = "CallViewModel"
    }

    private val _uiState = MutableStateFlow(CallUiState())
    val uiState: StateFlow<CallUiState> = _uiState.asStateFlow()

    init {
        observeSignalingEvents()
        setupIceCandidateCallback()
    }

    // ── Public actions ──────────────────────────────────────────────

    /**
     * Initiates an outgoing call in [roomId].
     * Triggers [WebRtcManager.createOffer] and emits the SDP via socket.
     */
    fun startCall(roomId: String) {
        _uiState.update { it.copy(callState = CallState.CALLING, roomId = roomId) }
        try {
            webRtcManager.createOffer { sdp ->
                socketManager.emitCallOffer(roomId, sdp)
                Log.i(TAG, "call_offer emitted roomId=$roomId")
            }
        } catch (e: Exception) {
            Log.e(TAG, "startCall error: ${e.message}", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            _uiState.update { it.copy(errorMessage = e.message) }
        }
    }

    /**
     * Accepts an incoming call represented by [offer].
     * Sets the remote SDP and creates an answer.
     */
    fun acceptCall(offer: CallOfferEvent) {
        _uiState.update { it.copy(callState = CallState.CALLING, roomId = offer.roomId) }
        try {
            webRtcManager.setRemoteOffer(offer.sdp)
            webRtcManager.createAnswer { sdp ->
                socketManager.emitCallAnswer(offer.roomId, sdp)
                Log.i(TAG, "call_answer emitted roomId=${offer.roomId}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "acceptCall error: ${e.message}", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            _uiState.update { it.copy(errorMessage = e.message) }
        }
    }

    /**
     * Rejects an incoming call for [roomId].
     */
    fun rejectCall(roomId: String) {
        socketManager.emitCallReject(roomId)
        _uiState.update { it.copy(callState = CallState.ENDED) }
        Log.i(TAG, "call_reject emitted roomId=$roomId")
    }

    /**
     * Ends the active call, releases WebRTC resources and notifies the remote peer.
     */
    fun endCall() {
        val roomId = _uiState.value.roomId
        if (roomId != null) socketManager.emitCallEnd(roomId)
        webRtcManager.release()
        _uiState.update { CallUiState(callState = CallState.IDLE) }
        Log.i(TAG, "call ended roomId=$roomId")
    }

    /** Toggles the microphone and updates [CallUiState.isMuted]. */
    fun toggleMic() {
        val muted = webRtcManager.toggleMic()
        _uiState.update { it.copy(isMuted = muted) }
    }

    /** Toggles the camera and updates [CallUiState.isCameraOff]. */
    fun toggleCamera() {
        val off = webRtcManager.toggleCamera()
        _uiState.update { it.copy(isCameraOff = off) }
    }

    /** Switches between front and back camera. */
    fun switchCamera() {
        webRtcManager.switchCamera()
        _uiState.update { it.copy(isFrontCamera = !it.isFrontCamera) }
    }

    // ── Private ─────────────────────────────────────────────────────

    private fun observeSignalingEvents() {
        // Incoming call offer → show incoming UI
        viewModelScope.launch {
            socketManager.callIncoming.collect { offer ->
                _uiState.update { it.copy(callState = CallState.INCOMING, roomId = offer.roomId) }
                Log.i(TAG, "call_incoming received roomId=${offer.roomId} from=${offer.fromUserId}")
            }
        }

        // Remote peer answered → set remote SDP and transition to CONNECTED
        viewModelScope.launch {
            socketManager.callAnswered.collect { event ->
                try {
                    webRtcManager.setRemoteAnswer(event.sdp)
                    _uiState.update { it.copy(callState = CallState.CONNECTED) }
                    Log.i(TAG, "call_answered — state=CONNECTED roomId=${event.roomId}")
                } catch (e: Exception) {
                    Log.e(TAG, "callAnswered error: ${e.message}", e)
                    FirebaseCrashlytics.getInstance().recordException(e)
                    _uiState.update { it.copy(errorMessage = e.message) }
                }
            }
        }

        // Remote peer rejected the call
        viewModelScope.launch {
            socketManager.callRejected.collect { event ->
                webRtcManager.release()
                _uiState.update { CallUiState(callState = CallState.ENDED) }
                Log.i(TAG, "call_rejected roomId=${event.roomId}")
            }
        }

        // Either party ended the call
        viewModelScope.launch {
            socketManager.callEnded.collect { event ->
                webRtcManager.release()
                _uiState.update { CallUiState(callState = CallState.ENDED) }
                Log.i(TAG, "call_ended roomId=${event.roomId}")
            }
        }

        // Remote ICE candidate
        viewModelScope.launch {
            socketManager.remoteIceCandidate.collect { event ->
                try {
                    webRtcManager.addRemoteIceCandidate(event.candidate)
                } catch (e: Exception) {
                    Log.e(TAG, "addRemoteIceCandidate error: ${e.message}", e)
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }
        }
    }

    private fun setupIceCandidateCallback() {
        webRtcManager.onIceCandidate = { candidate ->
            val roomId = _uiState.value.roomId ?: return@onIceCandidate
            socketManager.emitIceCandidate(roomId, candidate)
        }
    }

    override fun onCleared() {
        super.onCleared()
        webRtcManager.onIceCandidate = null
    }
}
