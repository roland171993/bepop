package com.stopgalere

import android.content.Context
import android.net.Uri
import android.speech.tts.TextToSpeech
import app.cash.turbine.test
import com.stopgalere.data.remote.dto.AiProvider
import com.stopgalere.data.remote.dto.GenerateCoverLetterRequest
import com.stopgalere.domain.usecase.ai.GenerateCoverLetterUseCase
import com.stopgalere.presentation.viewmodel.AiCoverLetterViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [AiCoverLetterViewModel].
 *
 * Uses [StandardTestDispatcher] + [runTest].
 * MockK mocks [GenerateCoverLetterUseCase] and [Context].
 * [TextToSpeech] constructor is mocked via mockkConstructor to avoid JVM crashes.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AiCoverLetterViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val generateCoverLetterUseCase = mockk<GenerateCoverLetterUseCase>()
    private val context = mockk<Context>(relaxed = true)
    private lateinit var viewModel: AiCoverLetterViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkConstructor(TextToSpeech::class)
        every { anyConstructed<TextToSpeech>().language = any() } just runs
        every { anyConstructed<TextToSpeech>().speak(any(), any(), any(), any()) } returns TextToSpeech.SUCCESS
        every { anyConstructed<TextToSpeech>().stop() } returns Unit
        every { anyConstructed<TextToSpeech>().shutdown() } returns Unit
        viewModel = AiCoverLetterViewModel(generateCoverLetterUseCase, context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkConstructors(TextToSpeech::class)
    }

    // ── Test 1: addImageUris updates selectedImageUris ────────────────

    @Test
    fun `addImageUris appends new URIs and deduplicates`() = runTest {
        val uri1 = mockk<Uri>()
        val uri2 = mockk<Uri>()

        viewModel.addImageUris(listOf(uri1, uri2))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.selectedImageUris.size)
        assertTrue(state.selectedImageUris.containsAll(listOf(uri1, uri2)))

        // Adding uri1 again should not duplicate
        viewModel.addImageUris(listOf(uri1))
        advanceUntilIdle()
        assertEquals(2, viewModel.uiState.value.selectedImageUris.size)
    }

    // ── Test 2: selectProvider updates selectedProvider to GEMINI ─────

    @Test
    fun `selectProvider updates selectedProvider`() = runTest {
        viewModel.selectProvider(AiProvider.GEMINI)
        advanceUntilIdle()

        assertEquals(AiProvider.GEMINI, viewModel.uiState.value.selectedProvider)
    }

    // ── Test 3: generate success → generatedText set, isLoading false ─

    @Test
    fun `generate success sets generatedText and clears isLoading`() = runTest {
        val request = slot<GenerateCoverLetterRequest>()
        coEvery { generateCoverLetterUseCase(capture(request)) } returns Result.success("Lettre générée avec succès.")

        viewModel.uiState.test {
            awaitItem() // initial state

            viewModel.generate("Développeur", "Description du poste", "Acme Corp")
            advanceUntilIdle()

            // consume intermediate loading state
            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertEquals("Lettre générée avec succès.", successState.generatedText)
            assertNull(successState.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ── Test 4: generate failure → error set, isLoading false ─────────

    @Test
    fun `generate failure sets error and clears isLoading`() = runTest {
        coEvery { generateCoverLetterUseCase(any()) } returns Result.failure(RuntimeException("API error"))

        viewModel.uiState.test {
            awaitItem() // initial

            viewModel.generate("Développeur", "Description", null)
            advanceUntilIdle()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertEquals("API error", errorState.error)
            assertEquals("", errorState.generatedText)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ── Test 5: updateVoiceTranscript sets voiceTranscript ────────────

    @Test
    fun `updateVoiceTranscript sets transcript and clears isRecording`() = runTest {
        viewModel.setRecording(true)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.isRecording)

        viewModel.updateVoiceTranscript("Je suis motivé par ce poste.")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Je suis motivé par ce poste.", state.voiceTranscript)
        assertFalse(state.isRecording)
    }

    // ── Test 6: clearError resets error to null ────────────────────────

    @Test
    fun `clearError resets error to null`() = runTest {
        coEvery { generateCoverLetterUseCase(any()) } returns Result.failure(RuntimeException("Erreur"))

        viewModel.generate("Dev", "Desc", null)
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.error)

        viewModel.clearError()
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.error)
    }
}
