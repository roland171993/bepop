package com.stopgalere

import app.cash.turbine.test
import com.stopgalere.data.remote.dto.CallAnswerEvent
import com.stopgalere.data.remote.dto.CallEndEvent
import com.stopgalere.data.remote.dto.CallOfferEvent
import com.stopgalere.data.remote.dto.CallRejectEvent
import com.stopgalere.data.remote.dto.IceCandidateDto
import com.stopgalere.data.remote.dto.IceCandidateEvent
import com.stopgalere.data.remote.socket.SocketManager
import com.stopgalere.data.remote.webrtc.WebRtcManager
import com.stopgalere.presentation.ui.call.CallState
import com.stopgalere.presentation.viewmodel.CallViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [CallViewModel].
 *
 * Uses [StandardTestDispatcher] + [runTest].
 * MockK mocks [SocketManager] and [WebRtcManager] — no real network or WebRTC.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CallViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    // ── Mocks ────────────────────────────────────────────────────────
    private val socketManager = mockk<SocketManager>(relaxed = true)
    private val webRtcManager = mockk<WebRtcManager>(relaxed = true)

    // ── Backing flows exposed by SocketManager ────────────────────
    private val callIncomingFlow      = MutableSharedFlow<CallOfferEvent>(extraBufferCapacity = 4)
    private val callAnsweredFlow      = MutableSharedFlow<CallAnswerEvent>(extraBufferCapacity = 4)
    private val callRejectedFlow      = MutableSharedFlow<CallRejectEvent>(extraBufferCapacity = 4)
    private val callEndedFlow         = MutableSharedFlow<CallEndEvent>(extraBufferCapacity = 4)
    private val remoteIceCandidateFlow = MutableSharedFlow<IceCandidateEvent>(extraBufferCapacity = 64)

    private lateinit var viewModel: CallViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { socketManager.callIncoming      } returns callIncomingFlow
        every { socketManager.callAnswered      } returns callAnsweredFlow
        every { socketManager.callRejected      } returns callRejectedFlow
        every { socketManager.callEnded         } returns callEndedFlow
        every { socketManager.remoteIceCandidate } returns remoteIceCandidateFlow

        viewModel = CallViewModel(
            socketManager = socketManager,
            webRtcManager = webRtcManager
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Test 1: startCall triggers createOffer and sets CALLING ──────

    @Test
    fun `startCall triggers webRtcManager_createOffer and state transitions to CALLING`() = runTest {
        viewModel.uiState.test {
            val initial = awaitItem()
            assertEquals(CallState.IDLE, initial.callState)

            viewModel.startCall("support-uid123")
            advanceUntilIdle()

            val calling = awaitItem()
            assertEquals(CallState.CALLING, calling.callState)
            assertEquals("support-uid123", calling.roomId)

            verify { webRtcManager.createOffer(any()) }
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ── Test 2: callAnswered event transitions to CONNECTED ──────────

    @Test
    fun `callAnswered event transitions state to CONNECTED`() = runTest {
        viewModel.startCall("support-uid123")
        advanceUntilIdle()

        callAnsweredFlow.emit(CallAnswerEvent(roomId = "support-uid123", sdp = "remote-answer-sdp"))
        advanceUntilIdle()

        assertEquals(CallState.CONNECTED, viewModel.uiState.value.callState)
        verify { webRtcManager.setRemoteAnswer("remote-answer-sdp") }
    }

    // ── Test 3: callRejected event transitions to ENDED ──────────────

    @Test
    fun `callRejected event transitions state to ENDED`() = runTest {
        viewModel.startCall("support-uid123")
        advanceUntilIdle()

        callRejectedFlow.emit(CallRejectEvent(roomId = "support-uid123"))
        advanceUntilIdle()

        assertEquals(CallState.ENDED, viewModel.uiState.value.callState)
        verify { webRtcManager.release() }
    }

    // ── Test 4: callEnded event transitions to ENDED ─────────────────

    @Test
    fun `callEnded event transitions state to ENDED`() = runTest {
        viewModel.startCall("support-uid123")
        advanceUntilIdle()

        callEndedFlow.emit(CallEndEvent(roomId = "support-uid123"))
        advanceUntilIdle()

        assertEquals(CallState.ENDED, viewModel.uiState.value.callState)
        verify { webRtcManager.release() }
    }

    // ── Test 5: acceptCall triggers setRemoteOffer + createAnswer ────

    @Test
    fun `acceptCall triggers setRemoteOffer and createAnswer`() = runTest {
        val offer = CallOfferEvent(
            roomId     = "support-uid456",
            fromUserId = "uid456",
            sdp        = "incoming-offer-sdp"
        )

        viewModel.acceptCall(offer)
        advanceUntilIdle()

        verify { webRtcManager.setRemoteOffer("incoming-offer-sdp") }
        verify { webRtcManager.createAnswer(any()) }
    }

    // ── Test 6: rejectCall emits call_reject via socket ──────────────

    @Test
    fun `rejectCall emits call_reject via socket and transitions to ENDED`() = runTest {
        viewModel.rejectCall("support-uid789")
        advanceUntilIdle()

        verify { socketManager.emitCallReject("support-uid789") }
        assertEquals(CallState.ENDED, viewModel.uiState.value.callState)
    }

    // ── Test 7: endCall releases webRtcManager and state is IDLE ─────

    @Test
    fun `endCall calls webRtcManager_release and state transitions to IDLE`() = runTest {
        // Start a call first so roomId is set
        viewModel.startCall("support-uid123")
        advanceUntilIdle()

        viewModel.endCall()
        advanceUntilIdle()

        verify { webRtcManager.release() }
        assertEquals(CallState.IDLE, viewModel.uiState.value.callState)
        assertNull(viewModel.uiState.value.roomId)
    }
}
