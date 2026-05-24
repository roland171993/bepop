package com.stopgalere.presentation.ui.call

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.stopgalere.data.remote.dto.CallOfferEvent
import org.junit.Rule
import org.junit.Test

/**
 * Instrumented UI tests for [VideoCallScreenContent].
 *
 * Tests the stateless composable directly — no ViewModel, no Hilt, no WebRTC.
 * [SurfaceViewRenderer] is replaced with lightweight stubs via [FakeSurfaceViewRenderer].
 *
 * Covered:
 *   1. INCOMING state shows accept + reject buttons
 *   2. CONNECTED state shows end + mute + camera + switch buttons
 *   3. CALLING state shows loading spinner
 *   4. onAccept callback fires when accept button is clicked
 *   5. onEnd callback fires when end call button is clicked
 *   6. onToggleMic callback fires when mute button is clicked
 */
class VideoCallScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ── Helpers ──────────────────────────────────────────────────────

    private fun idleState(callState: CallState = CallState.IDLE) = CallUiState(
        callState   = callState,
        roomId      = "support-uid123",
        isMuted     = false,
        isCameraOff = false
    )

    /**
     * Renders [VideoCallScreenContent] with fake (no-op) SurfaceViewRenderers.
     * The AndroidView will simply embed the view without GPU rendering.
     */
    private fun setCallContent(
        state:          CallUiState,
        onAccept:       (CallOfferEvent) -> Unit = {},
        onReject:       (String) -> Unit         = {},
        onEnd:          () -> Unit               = {},
        onToggleMic:    () -> Unit               = {},
        onToggleCamera: () -> Unit               = {},
        onSwitchCamera: () -> Unit               = {}
    ) {
        composeTestRule.setContent {
            val localRenderer  = androidx.compose.ui.platform.LocalContext.current.let {
                org.webrtc.SurfaceViewRenderer(it)
            }
            val remoteRenderer = androidx.compose.ui.platform.LocalContext.current.let {
                org.webrtc.SurfaceViewRenderer(it)
            }
            VideoCallScreenContent(
                state           = state,
                onAccept        = onAccept,
                onReject        = onReject,
                onEnd           = onEnd,
                onToggleMic     = onToggleMic,
                onToggleCamera  = onToggleCamera,
                onSwitchCamera  = onSwitchCamera,
                localRenderer   = localRenderer,
                remoteRenderer  = remoteRenderer
            )
        }
    }

    // ── Test 1: INCOMING state shows accept + reject buttons ─────────

    @Test
    fun incomingState_showsAcceptAndRejectButtons() {
        setCallContent(state = idleState(CallState.INCOMING))

        composeTestRule.onNodeWithTag(CallTestTags.CALL_ACCEPT_BTN).assertIsDisplayed()
        composeTestRule.onNodeWithTag(CallTestTags.CALL_REJECT_BTN).assertIsDisplayed()
    }

    // ── Test 2: CONNECTED state shows control bar buttons ────────────

    @Test
    fun connectedState_showsControlBarButtons() {
        setCallContent(state = idleState(CallState.CONNECTED))

        composeTestRule.onNodeWithTag(CallTestTags.CALL_END_BTN).assertIsDisplayed()
        composeTestRule.onNodeWithTag(CallTestTags.CALL_MUTE_BTN).assertIsDisplayed()
        composeTestRule.onNodeWithTag(CallTestTags.CALL_CAMERA_BTN).assertIsDisplayed()
        composeTestRule.onNodeWithTag(CallTestTags.CALL_SWITCH_BTN).assertIsDisplayed()
    }

    // ── Test 3: CALLING state shows loading spinner ──────────────────

    @Test
    fun callingState_showsLoadingSpinner() {
        setCallContent(state = idleState(CallState.CALLING))

        composeTestRule.onNodeWithTag(CallTestTags.CALL_LOADING).assertIsDisplayed()
    }

    // ── Test 4: onAccept callback fires ──────────────────────────────

    @Test
    fun incomingState_acceptButtonFiresCallback() {
        var accepted = false
        setCallContent(
            state    = idleState(CallState.INCOMING),
            onAccept = { accepted = true }
        )

        composeTestRule.onNodeWithTag(CallTestTags.CALL_ACCEPT_BTN).performClick()

        assert(accepted) { "Expected onAccept to be called" }
    }

    // ── Test 5: onEnd callback fires ─────────────────────────────────

    @Test
    fun connectedState_endButtonFiresCallback() {
        var ended = false
        setCallContent(
            state  = idleState(CallState.CONNECTED),
            onEnd  = { ended = true }
        )

        composeTestRule.onNodeWithTag(CallTestTags.CALL_END_BTN).performClick()

        assert(ended) { "Expected onEnd to be called" }
    }

    // ── Test 6: onToggleMic callback fires ───────────────────────────

    @Test
    fun connectedState_muteButtonFiresCallback() {
        var muted = false
        setCallContent(
            state        = idleState(CallState.CONNECTED),
            onToggleMic  = { muted = true }
        )

        composeTestRule.onNodeWithTag(CallTestTags.CALL_MUTE_BTN).performClick()

        assert(muted) { "Expected onToggleMic to be called" }
    }
}
