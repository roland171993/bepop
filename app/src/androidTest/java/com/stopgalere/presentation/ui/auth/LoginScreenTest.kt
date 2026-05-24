package com.stopgalere.presentation.ui.auth

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.stopgalere.data.remote.dto.UserDto
import org.junit.Rule
import org.junit.Test

/**
 * Instrumented UI tests for [LoginScreenContent].
 *
 * We test the stateless [LoginScreenContent] composable directly —
 * no ViewModel, no Hilt, no network — so these tests run quickly on any device/emulator.
 *
 * Covered:
 *  - Fields are rendered
 *  - Sign-In button is disabled when fields are empty
 *  - Sign-In button becomes enabled once both fields are filled
 *  - onLogin is called with correct arguments
 *  - Error message is shown when uiState has an error
 *  - Google and Apple buttons are visible
 *  - Navigate-to-register link is clickable
 */
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun idle() = AuthUiState()

    // ----------------------------------------------------------------
    // Rendering
    // ----------------------------------------------------------------
    @Test
    fun loginScreen_rendersAllElements() {
        composeTestRule.setContent {
            LoginScreenContent(
                uiState             = idle(),
                onLogin             = { _, _ -> },
                onGoogleSignIn      = {},
                onAppleSignIn       = {},
                onNavigateToRegister = {},
                onClearError        = {}
            )
        }

        composeTestRule.onNodeWithTag("login_email").assertIsDisplayed()
        composeTestRule.onNodeWithTag("login_password").assertIsDisplayed()
        composeTestRule.onNodeWithTag("login_btn").assertIsDisplayed()
        composeTestRule.onNodeWithTag("login_google_btn").assertIsDisplayed()
        composeTestRule.onNodeWithTag("login_apple_btn").assertIsDisplayed()
        composeTestRule.onNodeWithTag("login_register_link").assertIsDisplayed()
    }

    // ----------------------------------------------------------------
    // Button state
    // ----------------------------------------------------------------
    @Test
    fun loginButton_disabledWhenFieldsEmpty() {
        composeTestRule.setContent {
            LoginScreenContent(
                uiState             = idle(),
                onLogin             = { _, _ -> },
                onGoogleSignIn      = {},
                onAppleSignIn       = {},
                onNavigateToRegister = {},
                onClearError        = {}
            )
        }

        composeTestRule.onNodeWithTag("login_btn").assertIsNotEnabled()
    }

    @Test
    fun loginButton_enabledWhenFieldsFilled() {
        composeTestRule.setContent {
            LoginScreenContent(
                uiState             = idle(),
                onLogin             = { _, _ -> },
                onGoogleSignIn      = {},
                onAppleSignIn       = {},
                onNavigateToRegister = {},
                onClearError        = {}
            )
        }

        composeTestRule.onNodeWithTag("login_email").performTextInput("alice@example.com")
        composeTestRule.onNodeWithTag("login_password").performTextInput("Password1")

        composeTestRule.onNodeWithTag("login_btn").assertIsEnabled()
    }

    // ----------------------------------------------------------------
    // Callback
    // ----------------------------------------------------------------
    @Test
    fun loginButton_callsOnLoginWithCorrectValues() {
        var capturedEmail    = ""
        var capturedPassword = ""

        composeTestRule.setContent {
            LoginScreenContent(
                uiState             = idle(),
                onLogin             = { e, p -> capturedEmail = e; capturedPassword = p },
                onGoogleSignIn      = {},
                onAppleSignIn       = {},
                onNavigateToRegister = {},
                onClearError        = {}
            )
        }

        composeTestRule.onNodeWithTag("login_email").performTextInput("alice@example.com")
        composeTestRule.onNodeWithTag("login_password").performTextInput("Password1")
        composeTestRule.onNodeWithTag("login_btn").performClick()

        assert(capturedEmail == "alice@example.com")
        assert(capturedPassword == "Password1")
    }

    // ----------------------------------------------------------------
    // Error message
    // ----------------------------------------------------------------
    @Test
    fun errorMessage_displayedWhenPresent() {
        composeTestRule.setContent {
            LoginScreenContent(
                uiState             = AuthUiState(errorMessage = "Invalid credentials."),
                onLogin             = { _, _ -> },
                onGoogleSignIn      = {},
                onAppleSignIn       = {},
                onNavigateToRegister = {},
                onClearError        = {}
            )
        }

        composeTestRule.onNodeWithTag("login_error")
            .assertIsDisplayed()
            .assertTextContains("Invalid credentials.")
    }

    // ----------------------------------------------------------------
    // Loading state
    // ----------------------------------------------------------------
    @Test
    fun loginButton_disabledWhileLoading() {
        composeTestRule.setContent {
            LoginScreenContent(
                uiState             = AuthUiState(loading = true),
                onLogin             = { _, _ -> },
                onGoogleSignIn      = {},
                onAppleSignIn       = {},
                onNavigateToRegister = {},
                onClearError        = {}
            )
        }

        composeTestRule.onNodeWithTag("login_btn").assertIsNotEnabled()
    }

    // ----------------------------------------------------------------
    // Register navigation
    // ----------------------------------------------------------------
    @Test
    fun registerLink_triggersCallback() {
        var clicked = false

        composeTestRule.setContent {
            LoginScreenContent(
                uiState             = idle(),
                onLogin             = { _, _ -> },
                onGoogleSignIn      = {},
                onAppleSignIn       = {},
                onNavigateToRegister = { clicked = true },
                onClearError        = {}
            )
        }

        composeTestRule.onNodeWithTag("login_register_link").performClick()
        assert(clicked)
    }
}
