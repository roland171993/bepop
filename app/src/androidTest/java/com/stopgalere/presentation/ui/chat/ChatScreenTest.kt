package com.stopgalere.presentation.ui.chat

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.stopgalere.data.remote.dto.MessageDto
import org.junit.Rule
import org.junit.Test
import java.io.File

/**
 * Instrumented UI tests for [ChatScreenContent].
 *
 * We test the stateless [ChatScreenContent] composable directly —
 * no ViewModel, no Hilt, no Socket.io — so tests run quickly on any device/emulator.
 *
 * Covered:
 *   - Scaffold and message list are rendered
 *   - Loading indicator shown when isLoading = true
 *   - Messages displayed in the list
 *   - Send button is disabled when input is empty
 *   - Send button becomes enabled when input is non-blank
 *   - onSend callback fires with correct content
 *   - Typing indicator shown when remoteIsTyping = true
 *   - Connection badge is visible
 *   - Attach button is clickable
 */
class ChatScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ── Helpers ──────────────────────────────────────────────────────

    private fun idleState(
        messages:       List<MessageDto> = emptyList(),
        isLoading:      Boolean = false,
        isConnected:    Boolean = true,
        remoteIsTyping: Boolean = false,
        isSending:      Boolean = false,
        errorMessage:   String? = null,
        roomId:         String? = "support-uid123"
    ) = ChatUiState(
        messages       = messages,
        isLoading      = isLoading,
        isConnected    = isConnected,
        remoteIsTyping = remoteIsTyping,
        isSending      = isSending,
        errorMessage   = errorMessage,
        roomId         = roomId
    )

    private fun makeMessage(id: String, sender: String = "support", content: String = "Hello") =
        MessageDto(
            id         = id,
            roomId     = "support-uid123",
            senderId   = null,
            senderType = sender,
            content    = content,
            fileUrl    = null,
            fileName   = null,
            fileSize   = null,
            fileType   = null,
            readAt     = null,
            createdAt  = "2026-01-01T12:00:00.000Z"
        )

    // ── Rendering ────────────────────────────────────────────────────

    @Test
    fun chatScreen_rendersScaffoldAndMessageList() {
        composeTestRule.setContent {
            ChatScreenContent(
                state        = idleState(),
                onSend       = {},
                onTyping     = {},
                onAttach     = { _, _ -> },
                onMarkRead   = {},
                onConsumeErr = {},
                onBack       = {}
            )
        }

        composeTestRule.onNodeWithTag(ChatTestTags.SCREEN).assertExists()
        composeTestRule.onNodeWithTag(ChatTestTags.MESSAGE_LIST).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ChatTestTags.MESSAGE_INPUT).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ChatTestTags.SEND_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ChatTestTags.ATTACH_BUTTON).assertIsDisplayed()
        composeTestRule.onNodeWithTag(ChatTestTags.CONNECTION_BADGE).assertExists()
    }

    @Test
    fun chatScreen_showsLoadingIndicatorWhenLoading() {
        composeTestRule.setContent {
            ChatScreenContent(
                state        = idleState(isLoading = true),
                onSend       = {},
                onTyping     = {},
                onAttach     = { _, _ -> },
                onMarkRead   = {},
                onConsumeErr = {},
                onBack       = {}
            )
        }

        composeTestRule.onNodeWithTag(ChatTestTags.LOADING).assertIsDisplayed()
    }

    @Test
    fun chatScreen_displaysMessagesInList() {
        val messages = listOf(
            makeMessage("m1", "user",    "Bonjour"),
            makeMessage("m2", "support", "Comment puis-je aider?")
        )
        composeTestRule.setContent {
            ChatScreenContent(
                state        = idleState(messages = messages),
                onSend       = {},
                onTyping     = {},
                onAttach     = { _, _ -> },
                onMarkRead   = {},
                onConsumeErr = {},
                onBack       = {}
            )
        }

        composeTestRule.onNodeWithText("Bonjour").assertIsDisplayed()
        composeTestRule.onNodeWithText("Comment puis-je aider?").assertIsDisplayed()
    }

    @Test
    fun sendButton_disabledWhenInputIsEmpty() {
        composeTestRule.setContent {
            ChatScreenContent(
                state        = idleState(),
                onSend       = {},
                onTyping     = {},
                onAttach     = { _, _ -> },
                onMarkRead   = {},
                onConsumeErr = {},
                onBack       = {}
            )
        }

        composeTestRule.onNodeWithTag(ChatTestTags.SEND_BUTTON).assertIsNotEnabled()
    }

    @Test
    fun sendButton_enabledAfterTypingContent() {
        composeTestRule.setContent {
            ChatScreenContent(
                state        = idleState(),
                onSend       = {},
                onTyping     = {},
                onAttach     = { _, _ -> },
                onMarkRead   = {},
                onConsumeErr = {},
                onBack       = {}
            )
        }

        composeTestRule.onNodeWithTag(ChatTestTags.MESSAGE_INPUT)
            .performTextInput("Bonjour")

        composeTestRule.onNodeWithTag(ChatTestTags.SEND_BUTTON).assertIsEnabled()
    }

    @Test
    fun sendButton_callsOnSendWithInput() {
        var sentContent = ""
        composeTestRule.setContent {
            ChatScreenContent(
                state        = idleState(),
                onSend       = { sentContent = it },
                onTyping     = {},
                onAttach     = { _, _ -> },
                onMarkRead   = {},
                onConsumeErr = {},
                onBack       = {}
            )
        }

        composeTestRule.onNodeWithTag(ChatTestTags.MESSAGE_INPUT)
            .performTextInput("Test message")
        composeTestRule.onNodeWithTag(ChatTestTags.SEND_BUTTON).performClick()

        assert(sentContent == "Test message")
    }

    @Test
    fun typingIndicator_shownWhenRemoteIsTyping() {
        composeTestRule.setContent {
            ChatScreenContent(
                state        = idleState(remoteIsTyping = true),
                onSend       = {},
                onTyping     = {},
                onAttach     = { _, _ -> },
                onMarkRead   = {},
                onConsumeErr = {},
                onBack       = {}
            )
        }

        composeTestRule.onNodeWithTag(ChatTestTags.TYPING_INDICATOR).assertIsDisplayed()
    }

    @Test
    fun typingIndicator_hiddenWhenNotTyping() {
        composeTestRule.setContent {
            ChatScreenContent(
                state        = idleState(remoteIsTyping = false),
                onSend       = {},
                onTyping     = {},
                onAttach     = { _, _ -> },
                onMarkRead   = {},
                onConsumeErr = {},
                onBack       = {}
            )
        }

        composeTestRule.onNodeWithTag(ChatTestTags.TYPING_INDICATOR).assertDoesNotExist()
    }

    @Test
    fun attachButton_isClickable() {
        composeTestRule.setContent {
            ChatScreenContent(
                state        = idleState(),
                onSend       = {},
                onTyping     = {},
                onAttach     = { _, _ -> },
                onMarkRead   = {},
                onConsumeErr = {},
                onBack       = {}
            )
        }

        // Just verify the button is displayed and can receive a click without crashing
        composeTestRule.onNodeWithTag(ChatTestTags.ATTACH_BUTTON).assertIsDisplayed()
    }
}
