package com.stopgalere.data.remote.webrtc

import android.content.Context
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.stopgalere.data.remote.dto.IceCandidateDto
import dagger.hilt.android.qualifiers.ApplicationContext
import org.webrtc.AudioSource
import org.webrtc.AudioTrack
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraVideoCapturer
import org.webrtc.DataChannel
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.RtpReceiver
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import org.webrtc.SurfaceTextureHelper
import org.webrtc.VideoSink
import org.webrtc.VideoSource
import org.webrtc.VideoTrack
import javax.inject.Inject
import javax.inject.Singleton

/**
 * WebRtcManager
 *
 * Singleton wrapper around the stream-webrtc-android / org.webrtc API.
 *
 * Responsibilities:
 *   - Initialise PeerConnectionFactory once per process
 *   - Create a PeerConnection with Google STUN servers
 *   - Capture local camera + microphone into a MediaStream
 *   - Drive the WebRTC offer/answer/ICE exchange
 *   - Expose [onIceCandidate] callback so the ViewModel can relay ICE via socket
 *
 * No Activity/Context is stored as a field — only ApplicationContext is used.
 */
@Singleton
class WebRtcManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val TAG           = "WebRtcManager"
        private const val LOCAL_STREAM  = "local_stream"
        private const val VIDEO_TRACK   = "video_track"
        private const val AUDIO_TRACK   = "audio_track"

        private val STUN_SERVERS = listOf(
            "stun:stun.l.google.com:19302",
            "stun:stun1.l.google.com:19302"
        )
    }

    // ── Callback set by ViewModel ────────────────────────────────────
    var onIceCandidate: ((IceCandidateDto) -> Unit)? = null

    // ── WebRTC internals ─────────────────────────────────────────────
    private val eglBase: EglBase = EglBase.create()
    private var factory:         PeerConnectionFactory? = null
    private var peerConnection:  PeerConnection?        = null
    private var localStream:     MediaStream?           = null
    private var videoSource:     VideoSource?           = null
    private var audioSource:     AudioSource?           = null
    private var localVideoTrack: VideoTrack?            = null
    private var localAudioTrack: AudioTrack?            = null
    private var videoCapturer:   CameraVideoCapturer?   = null
    private var isMicMuted:      Boolean                = false
    private var isCameraOff:     Boolean                = false
    private var isFrontCamera:   Boolean                = true

    init {
        initFactory()
    }

    // ── Initialisation ────────────────────────────────────────────────

    private fun initFactory() {
        try {
            PeerConnectionFactory.initialize(
                PeerConnectionFactory.InitializationOptions.builder(context)
                    .setEnableInternalTracer(false)
                    .createInitializationOptions()
            )

            factory = PeerConnectionFactory.builder()
                .setVideoEncoderFactory(DefaultVideoEncoderFactory(eglBase.eglBaseContext, true, true))
                .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBase.eglBaseContext))
                .createPeerConnectionFactory()

            Log.i(TAG, "PeerConnectionFactory initialised")
        } catch (e: Exception) {
            Log.e(TAG, "initFactory failed: ${e.message}", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun buildPeerConnection(): PeerConnection? {
        val iceServers = STUN_SERVERS.map { uri ->
            PeerConnection.IceServer.builder(uri).createIceServer()
        }
        val rtcConfig = PeerConnection.RTCConfiguration(iceServers).apply {
            sdpSemantics   = PeerConnection.SdpSemantics.UNIFIED_PLAN
            continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
        }

        return factory?.createPeerConnection(rtcConfig, object : PeerConnection.Observer {
            override fun onIceCandidate(candidate: IceCandidate) {
                Log.d(TAG, "onIceCandidate mid=${candidate.sdpMid}")
                onIceCandidate?.invoke(
                    IceCandidateDto(
                        sdpMid        = candidate.sdpMid,
                        sdpMLineIndex = candidate.sdpMLineIndex,
                        candidate     = candidate.sdp
                    )
                )
            }
            override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>?) = Unit
            override fun onSignalingChange(state: PeerConnection.SignalingState?)      = Unit
            override fun onIceConnectionChange(state: PeerConnection.IceConnectionState?) {
                Log.d(TAG, "onIceConnectionChange $state")
            }
            override fun onIceConnectionReceivingChange(receiving: Boolean)            = Unit
            override fun onIceGatheringChange(state: PeerConnection.IceGatheringState?) = Unit
            override fun onAddStream(stream: MediaStream?)                              = Unit
            override fun onRemoveStream(stream: MediaStream?)                           = Unit
            override fun onDataChannel(channel: DataChannel?)                          = Unit
            override fun onRenegotiationNeeded()                                        = Unit
            override fun onAddTrack(receiver: RtpReceiver?, streams: Array<out MediaStream>?) = Unit
        })
    }

    // ── Local stream ──────────────────────────────────────────────────

    /**
     * Captures camera + mic and attaches the local video to [localRenderer].
     * Must be called before [createOffer] or [createAnswer].
     */
    fun initLocalStream(localRenderer: VideoSink) {
        try {
            val f = factory ?: return

            // Video capture
            val enumerator  = Camera2Enumerator(context)
            val frontCamera = enumerator.deviceNames.firstOrNull { enumerator.isFrontFacing(it) }
            val backCamera  = enumerator.deviceNames.firstOrNull { enumerator.isBackFacing(it) }
            val cameraName  = frontCamera ?: backCamera ?: return

            videoCapturer = enumerator.createCapturer(cameraName, null)

            val surfaceHelper = SurfaceTextureHelper.create("CaptureThread", eglBase.eglBaseContext)
            videoSource = f.createVideoSource(false)
            videoCapturer?.initialize(surfaceHelper, context, videoSource?.capturerObserver)
            videoCapturer?.startCapture(1280, 720, 30)

            localVideoTrack = f.createVideoTrack(VIDEO_TRACK, videoSource).also {
                it.addSink(localRenderer)
            }

            // Audio capture
            val audioConstraints = MediaConstraints()
            audioSource     = f.createAudioSource(audioConstraints)
            localAudioTrack = f.createAudioTrack(AUDIO_TRACK, audioSource)

            // Build or reuse peer connection and add tracks
            if (peerConnection == null) peerConnection = buildPeerConnection()
            localStream = f.createLocalMediaStream(LOCAL_STREAM).apply {
                addTrack(localVideoTrack)
                addTrack(localAudioTrack)
            }
            peerConnection?.addStream(localStream)

            Log.i(TAG, "Local stream initialised")
        } catch (e: Exception) {
            Log.e(TAG, "initLocalStream failed: ${e.message}", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    /** Attaches a remote video renderer to any incoming video track. */
    fun setRemoteRenderer(remoteRenderer: VideoSink) {
        peerConnection?.transceivers?.forEach { transceiver ->
            transceiver.receiver?.track()?.let { track ->
                if (track is VideoTrack) track.addSink(remoteRenderer)
            }
        }
    }

    // ── Offer / Answer ────────────────────────────────────────────────

    /**
     * Creates an SDP offer and returns the SDP string via [onSuccess].
     * Ensure [initLocalStream] has been called first.
     */
    fun createOffer(onSuccess: (String) -> Unit) {
        if (peerConnection == null) peerConnection = buildPeerConnection()
        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        }
        peerConnection?.createOffer(sdpObserver(
            onCreateSuccess = { sdp ->
                peerConnection?.setLocalDescription(sdpObserver(), sdp)
                onSuccess(sdp.description)
            }
        ), constraints)
    }

    /**
     * Sets the remote SDP offer received from the caller.
     * Call before [createAnswer].
     */
    fun setRemoteOffer(sdp: String) {
        val sessionDescription = SessionDescription(SessionDescription.Type.OFFER, sdp)
        peerConnection?.setRemoteDescription(sdpObserver(), sessionDescription)
    }

    /**
     * Creates an SDP answer and returns the SDP string via [onSuccess].
     * Call after [setRemoteOffer].
     */
    fun createAnswer(onSuccess: (String) -> Unit) {
        val constraints = MediaConstraints()
        peerConnection?.createAnswer(sdpObserver(
            onCreateSuccess = { sdp ->
                peerConnection?.setLocalDescription(sdpObserver(), sdp)
                onSuccess(sdp.description)
            }
        ), constraints)
    }

    /**
     * Sets the remote SDP answer received from the callee.
     */
    fun setRemoteAnswer(sdp: String) {
        val sessionDescription = SessionDescription(SessionDescription.Type.ANSWER, sdp)
        peerConnection?.setRemoteDescription(sdpObserver(), sessionDescription)
    }

    /**
     * Adds a remote ICE candidate received from the other peer.
     */
    fun addRemoteIceCandidate(candidate: IceCandidateDto) {
        val iceCandidate = IceCandidate(
            candidate.sdpMid ?: "",
            candidate.sdpMLineIndex,
            candidate.candidate
        )
        peerConnection?.addIceCandidate(iceCandidate)
    }

    // ── Controls ──────────────────────────────────────────────────────

    /** Switches between front and back camera. */
    fun switchCamera() {
        (videoCapturer as? CameraVideoCapturer)?.switchCamera(null)
        isFrontCamera = !isFrontCamera
    }

    /**
     * Toggles microphone mute.
     * @return true if the mic is now muted, false if unmuted.
     */
    fun toggleMic(): Boolean {
        isMicMuted = !isMicMuted
        localAudioTrack?.setEnabled(!isMicMuted)
        return isMicMuted
    }

    /**
     * Toggles local camera on/off.
     * @return true if camera is now off, false if on.
     */
    fun toggleCamera(): Boolean {
        isCameraOff = !isCameraOff
        localVideoTrack?.setEnabled(!isCameraOff)
        return isCameraOff
    }

    // ── Cleanup ───────────────────────────────────────────────────────

    /**
     * Releases all WebRTC resources. Call when the call screen is destroyed.
     */
    fun release() {
        try {
            videoCapturer?.stopCapture()
            videoCapturer?.dispose()
            videoCapturer = null

            localVideoTrack?.dispose()
            localVideoTrack = null

            localAudioTrack?.dispose()
            localAudioTrack = null

            videoSource?.dispose()
            videoSource = null

            audioSource?.dispose()
            audioSource = null

            localStream = null

            peerConnection?.close()
            peerConnection?.dispose()
            peerConnection = null

            onIceCandidate = null

            Log.i(TAG, "WebRtcManager released")
        } catch (e: Exception) {
            Log.e(TAG, "release error: ${e.message}", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private fun sdpObserver(
        onCreateSuccess: ((SessionDescription) -> Unit)? = null
    ): SdpObserver = object : SdpObserver {
        override fun onCreateSuccess(sdp: SessionDescription) {
            onCreateSuccess?.invoke(sdp)
        }
        override fun onSetSuccess()                                  = Unit
        override fun onCreateFailure(error: String?) {
            Log.e(TAG, "SDP createFailure: $error")
        }
        override fun onSetFailure(error: String?) {
            Log.e(TAG, "SDP setFailure: $error")
        }
    }
}
