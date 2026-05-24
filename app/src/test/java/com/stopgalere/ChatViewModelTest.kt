package com.stopgalere

import app.cash.turbine.test
import com.stopgalere.data.local.Prefs
import com.stopgalere.data.remote.dto.MessageDto
import com.stopgalere.data.remote.socket.SocketManager
import com.stopgalere.domain.usecase.chat.GetMessagesUseCase
import com.stopgalere.domain.usecase.chat.UploadChatFileUseCase
import com.stopgalere.presentation.viewmodel.ChatViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * Unit tests for [ChatViewModel].
 *
 * Uses [StandardTestDispatcher] + [runTest].
 * MockK mocks SocketManager, use-cases and Prefs — no real network or socket.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    // ── Mocks ────────────────────────────────────────────────────────
    private val socketManager     = mockk<SocketManager>(relaxed = true)
    private val getMessagesUseCase = mockk<GetMessagesUseCase>()
    private val uploadFileUseCase  = mockk<UploadChatFileUseCase>()
    private val prefs              = mockk<Prefs>(relaxed = true)

    // Backing flows exposed by SocketManager
    private val isConnectedFlow  = MutableStateFlow(false)
    private val newMessagesFlow  = MutableSharedFlow<MessageDto>(extraBufferCapacity = 64)
    private val userTypingFlow   = MutableSharedFlow<com.stopgalere.data.remote.dto.UserTypingEvent>(extraBufferCapacity = 8)
    private val messagesReadFlow = MutableSharedFlow<com.stopgalere.data.remote.dto.MessagesReadEvent>(extraBufferCapacity = 8)

    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { socketManager.isConnected  } returns isConnectedFlow
        every { socketManager.newMessages  } returns newMessagesFlow
        every { socketManager.userTyping   } returns userTypingFlow
        every { socketManager.messagesRead } returns messagesReadFlow

        viewModel = ChatViewModel(
            socketManager      = socketManager,
            getMessagesUseCase = getMessagesUseCase,
            uploadFileUseCase  = uploadFileUseCase,
            prefs              = prefs
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── Helpers ──────────────────────────────────────────────────────
    private fun makeMessage(id: String = "msg1", senderType: String = "support") = MessageDto(
        id         = id,
        roomId     = "support-uid123",
        senderId   = null,
        senderType = senderType,
        content    = "Hello",
        fileUrl    = null,
        fileName   = null,
        fileSize   = null,
        fileType   = null,
        readAt     = null,
        createdAt  = "2026-01-01T12:00:00.000Z"
    )

    // ── Tests ────────────────────────────────────────────────────────

    @Test
    fun `enterRoom sets roomId and calls connect + joinRoom`() = runTest {
        coEvery { getMessagesUseCase(any(), any(), any()) } returns emptyList()

        viewModel.enterRoom("support-uid123")
        advanceUntilIdle()

        assertEquals("support-uid123", viewModel.uiState.value.roomId)
        verify { socketManager.connect() }
        verify { socketManager.joinRoom("support-uid123") }
    }

    @Test
    fun `enterRoom loads message history`() = runTest {
        val messages = listOf(makeMessage("m1"), makeMessage("m2"))
        coEvery { getMessagesUseCase("support-uid123", null, null) } returns messages

        viewModel.enterRoom("support-uid123")
        advanceUntilIdle()

        assertEquals(messages, viewModel.uiState.value.messages)
    }

    @Test
    fun `sendMessage calls socketManager_sendMessage`() = runTest {
        coEvery { getMessagesUseCase(any(), any(), any()) } returns emptyList()
        viewModel.enterRoom("support-uid123")
        advanceUntilIdle()

        viewModel.sendMessage("Bonjour!")

        verify { socketManager.sendMessage("support-uid123", "Bonjour!") }
    }

    @Test
    fun `sendMessage ignores blank content`() = runTest {
        coEvery { getMessagesUseCase(any(), any(), any()) } returns emptyList()
        viewModel.enterRoom("support-uid123")
        advanceUntilIdle()

        viewModel.sendMessage("   ")

        verify(exactly = 0) { socketManager.sendMessage(any(), any()) }
    }

    @Test
    fun `new socket message is appended to uiState`() = runTest {
        coEvery { getMessagesUseCase(any(), any(), any()) } returns emptyList()
        viewModel.enterRoom("support-uid123")
        advanceUntilIdle()

        val msg = makeMessage("incoming-1")
        newMessagesFlow.emit(msg)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.messages.any { it.id == "incoming-1" })
    }

    @Test
    fun `duplicate socket message is not added twice`() = runTest {
        coEvery { getMessagesUseCase(any(), any(), any()) } returns emptyList()
        viewModel.enterRoom("support-uid123")
        advanceUntilIdle()

        val msg = makeMessage("dup-msg")
        newMessagesFlow.emit(msg)
        newMessagesFlow.emit(msg) // second emit
        advanceUntilIdle()

        val count = viewModel.uiState.value.messages.count { it.id == "dup-msg" }
        assertEquals(1, count)
    }

    @Test
    fun `isConnected state mirrors SocketManager flow`() = runTest {
        isConnectedFlow.emit(true)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isConnected)

        isConnectedFlow.emit(false)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isConnected)
    }

    @Test
    fun `uploadFile error sets errorMessage in uiState`() = runTest {
        coEvery { getMessagesUseCase(any(), any(), any()) } returns emptyList()
        viewModel.enterRoom("support-uid123")
        advanceUntilIdle()

        coEvery { uploadFileUseCase(any(), any(), any()) } throws RuntimeException("Network error")
        val file = mockk<File>(relaxed = true)
        every { file.name } returns "test.jpg"

        viewModel.uploadFile(file, "image/jpeg")
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isSending)
    }
}
