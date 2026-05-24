package com.stopgalere

import android.content.Context
import android.net.Uri
import android.speech.tts.TextToSpeech
import app.cash.turbine.test
import com.stopgalere.data.remote.dto.AiProvider
import com.stopgalere.domain.usecase.ai.GenerateProPhotoUseCase
import com.stopgalere.presentation.viewmodel.AiProPhotoViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [AiProPhotoViewModel].
 *
 * Uses [StandardTestDispatcher] + [runTest].
 * MockK mocks [GenerateProPhotoUseCase] and [Context].
 * [TextToSpeech] constructor is mocked via mockkConstructor to avoid JVM crashes.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AiProPhotoViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val generateProPhotoUseCase = mockk<GenerateProPhotoUseCase>()
    private val context = mockk<Context>(relaxed = true)
    private lateinit var viewModel: AiProPhotoViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkConstructor(TextToSpeech::class)
        every { anyConstructed<TextToSpeech>().language = any() } just runs
        every { anyConstructed<TextToSpeech>().speak(any(), any(), any(), any()) } returns TextToSpeech.SUCCESS
        every { anyConstructed<TextToSpeech>().stop() } returns Unit
        every { anyConstructed<TextToSpeech>().shutdown() } returns Unit
        viewModel = AiProPhotoViewModel(generateProPhotoUseCase, context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkConstructors(TextToSpeech::class)
    }

    // ── Test 1: setSelectedImage updates selectedImageUri and clears croppedImageUri ──

    @Test
    fun `setSelectedImage updates selectedImageUri and resets croppedImageUri`() = runTest {
        val uri = mockk<Uri>()
        val croppedUri = mockk<Uri>()

        viewModel.setCroppedImage(croppedUri)
        advanceUntilIdle()
        assertNotNull(viewModel.uiState.value.croppedImageUri)

        viewModel.setSelectedImage(uri)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(uri, state.selectedImageUri)
        assertNull(state.croppedImageUri)
    }

    // ── Test 2: setCroppedImage updates croppedImageUri ───────────────

    @Test
    fun `setCroppedImage updates croppedImageUri`() = runTest {
        val cropped = mockk<Uri>()
        viewModel.setCroppedImage(cropped)
        advanceUntilIdle()

        assertEquals(cropped, viewModel.uiState.value.croppedImageUri)
    }

    // ── Test 3: setWithSuit toggles withSuit flag ─────────────────────

    @Test
    fun `setWithSuit toggles costume flag`() = runTest {
        assertTrue(viewModel.uiState.value.withSuit) // default true

        viewModel.setWithSuit(false)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.withSuit)

        viewModel.setWithSuit(true)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.withSuit)
    }

    // ── Test 4: generate success sets generatedPhotoUrl ───────────────

    @Test
    fun `generate success sets generatedPhotoUrl and clears isLoading`() = runTest {
        coEvery {
            generateProPhotoUseCase(
                photoUri = any(),
                style = any(),
                voiceDescription = any(),
                withSuit = any(),
                provider = any(),
                jobTitle = any(),
                sendToEmail = any()
            )
        } returns Result.success("/uploads/photos/generated.jpg")

        viewModel.uiState.test {
            awaitItem() // initial

            viewModel.generate("Développeur Senior")
            advanceUntilIdle()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertEquals("/uploads/photos/generated.jpg", successState.generatedPhotoUrl)
            assertNull(successState.error)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ── Test 5: generate failure sets error and clears isLoading ──────

    @Test
    fun `generate failure sets error and clears isLoading`() = runTest {
        coEvery {
            generateProPhotoUseCase(
                photoUri = any(),
                style = any(),
                voiceDescription = any(),
                withSuit = any(),
                provider = any(),
                jobTitle = any(),
                sendToEmail = any()
            )
        } returns Result.failure(RuntimeException("DALL·E error"))

        viewModel.uiState.test {
            awaitItem()

            viewModel.generate("Ingénieur")
            advanceUntilIdle()

            val loadingState = awaitItem()
            assertTrue(loadingState.isLoading)

            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertEquals("DALL·E error", errorState.error)
            assertEquals("", errorState.generatedPhotoUrl)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ── Test 6: selectProvider changes provider ────────────────────────

    @Test
    fun `selectProvider updates selectedProvider to DEEPSEEK`() = runTest {
        viewModel.selectProvider(AiProvider.DEEPSEEK)
        advanceUntilIdle()

        assertEquals(AiProvider.DEEPSEEK, viewModel.uiState.value.selectedProvider)
    }

    // ── Test 7: clearError resets error to null ────────────────────────

    @Test
    fun `clearError resets error to null`() = runTest {
        coEvery {
            generateProPhotoUseCase(any(), any(), any(), any(), any(), any(), any())
        } returns Result.failure(RuntimeException("Erreur réseau"))

        viewModel.generate("Dev")
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.error)

        viewModel.clearError()
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.error)
    }
}
