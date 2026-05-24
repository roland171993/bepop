package com.stopgalere

import app.cash.turbine.test
import com.stopgalere.data.remote.dto.AuthResponse
import com.stopgalere.data.remote.dto.UpdateProfileRequest
import com.stopgalere.data.remote.dto.UserDto
import com.stopgalere.domain.repository.AuthRepoInterface
import com.stopgalere.domain.usecase.auth.GetProfileUseCase
import com.stopgalere.domain.usecase.auth.LoginUseCase
import com.stopgalere.domain.usecase.auth.RegisterUseCase
import com.stopgalere.domain.usecase.auth.UpdateProfileUseCase
import com.stopgalere.presentation.ui.auth.AuthSuccessEvent
import com.stopgalere.presentation.viewmodel.AuthViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.MultipartBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [AuthViewModel].
 *
 * Uses [StandardTestDispatcher] + [runTest] to control coroutine execution.
 * MockK mocks all use-cases and repository — no real network calls.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    // ----------------------------------------------------------------
    // Test coroutine dispatcher
    // ----------------------------------------------------------------
    private val testDispatcher = StandardTestDispatcher()

    // ----------------------------------------------------------------
    // Mocks
    // ----------------------------------------------------------------
    private val repo              = mockk<AuthRepoInterface>()
    private val loginUseCase      = mockk<LoginUseCase>()
    private val registerUseCase   = mockk<RegisterUseCase>()
    private val getProfileUseCase = mockk<GetProfileUseCase>()
    private val updateProfileUseCase = mockk<UpdateProfileUseCase>()

    private lateinit var viewModel: AuthViewModel

    // ----------------------------------------------------------------
    // Fixtures
    // ----------------------------------------------------------------
    private val fakeUser = UserDto(
        id           = "u1",
        firstName    = "Alice",
        lastName     = "Doe",
        age          = 30,
        email        = "alice@example.com",
        phone        = "+2250700000000",
        photoUrl     = null,
        authProvider = "local",
        role         = "user",
        createdAt    = "2026-01-01T00:00:00Z"
    )
    private val fakeAuthResponse = AuthResponse(token = "fake.jwt.token", user = fakeUser)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(
            repo, loginUseCase, registerUseCase, getProfileUseCase, updateProfileUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ----------------------------------------------------------------
    // login
    // ----------------------------------------------------------------
    @Test
    fun `login success emits LoggedIn event and sets user`() = runTest {
        coEvery { loginUseCase("alice@example.com", "Password1") } returns fakeAuthResponse

        viewModel.uiState.test {
            val initial = awaitItem()
            assertFalse(initial.loading)
            assertNull(initial.user)

            viewModel.login("alice@example.com", "Password1")
            testScheduler.advanceUntilIdle()

            val loadingState = awaitItem()   // loading = true
            assertTrue(loadingState.loading)

            val successState = awaitItem()   // loading = false, user set
            assertFalse(successState.loading)
            assertEquals(fakeUser, successState.user)
            assertEquals(AuthSuccessEvent.LoggedIn, successState.successEvent)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `login failure sets errorMessage`() = runTest {
        coEvery { loginUseCase(any(), any()) } throws Exception("Invalid credentials.")

        viewModel.uiState.test {
            awaitItem() // initial
            viewModel.login("bad@example.com", "wrong")
            testScheduler.advanceUntilIdle()

            awaitItem() // loading true
            val errorState = awaitItem()
            assertFalse(errorState.loading)
            assertNotNull(errorState.errorMessage)
            assertTrue(errorState.errorMessage!!.contains("Invalid"))

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ----------------------------------------------------------------
    // register
    // ----------------------------------------------------------------
    @Test
    fun `register success emits LoggedIn event`() = runTest {
        coEvery {
            registerUseCase("Alice", "Doe", 30, "alice@example.com", "+225", "Password1")
        } returns fakeAuthResponse

        viewModel.uiState.test {
            awaitItem() // initial
            viewModel.register("Alice", "Doe", 30, "alice@example.com", "+225", "Password1")
            testScheduler.advanceUntilIdle()

            awaitItem() // loading
            val result = awaitItem()
            assertEquals(AuthSuccessEvent.LoggedIn, result.successEvent)
            assertEquals(fakeUser, result.user)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ----------------------------------------------------------------
    // oauthSignIn
    // ----------------------------------------------------------------
    @Test
    fun `oauthSignIn success emits LoggedIn event`() = runTest {
        coEvery { repo.oauthSignIn("firebase.id.token") } returns fakeAuthResponse

        viewModel.uiState.test {
            awaitItem()
            viewModel.oauthSignIn("firebase.id.token")
            testScheduler.advanceUntilIdle()

            awaitItem() // loading
            val result = awaitItem()
            assertEquals(AuthSuccessEvent.LoggedIn, result.successEvent)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ----------------------------------------------------------------
    // loadProfile
    // ----------------------------------------------------------------
    @Test
    fun `loadProfile sets user in state`() = runTest {
        coEvery { getProfileUseCase() } returns fakeUser

        viewModel.uiState.test {
            awaitItem()
            viewModel.loadProfile()
            testScheduler.advanceUntilIdle()

            awaitItem() // loading
            val result = awaitItem()
            assertEquals(fakeUser, result.user)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ----------------------------------------------------------------
    // updateProfile
    // ----------------------------------------------------------------
    @Test
    fun `updateProfile emits ProfileUpdated event`() = runTest {
        val updated = fakeUser.copy(firstName = "Bob", age = 35)
        coEvery { updateProfileUseCase("Bob", null, 35, null) } returns updated

        viewModel.uiState.test {
            awaitItem()
            viewModel.updateProfile("Bob", null, 35, null)
            testScheduler.advanceUntilIdle()

            awaitItem() // loading
            val result = awaitItem()
            assertEquals(AuthSuccessEvent.ProfileUpdated, result.successEvent)
            assertEquals("Bob", result.user?.firstName)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ----------------------------------------------------------------
    // logout
    // ----------------------------------------------------------------
    @Test
    fun `logout calls repo logout and emits LoggedOut`() = runTest {
        every { repo.logout() } returns Unit

        viewModel.uiState.test {
            awaitItem()
            viewModel.logout()

            val result = awaitItem()
            assertEquals(AuthSuccessEvent.LoggedOut, result.successEvent)
            assertNull(result.user)

            verify { repo.logout() }
            cancelAndIgnoreRemainingEvents()
        }
    }

    // ----------------------------------------------------------------
    // consumeSuccessEvent
    // ----------------------------------------------------------------
    @Test
    fun `consumeSuccessEvent clears successEvent`() = runTest {
        coEvery { loginUseCase(any(), any()) } returns fakeAuthResponse

        viewModel.uiState.test {
            awaitItem()
            viewModel.login("alice@example.com", "Password1")
            testScheduler.advanceUntilIdle()

            awaitItem()  // loading
            val withEvent = awaitItem()
            assertNotNull(withEvent.successEvent)

            viewModel.consumeSuccessEvent()
            val cleared = awaitItem()
            assertNull(cleared.successEvent)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
