package com.stopgalere.presentation.ui.intro

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.assertIsDisplayed
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test

class WizardPagerScreenComposeTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun wizard_navigates_to_main_on_finish() {
        composeRule.setContent { TestNavHost() }

        // There are 3 pages. Click twice for Next, then once for Finish.
        composeRule.onNodeWithTag("WizardNextButton").performClick()
        composeRule.onNodeWithTag("WizardNextButton").performClick()
        composeRule.onNodeWithTag("WizardNextButton").performClick()

        composeRule.onNodeWithTag("MainScreen").assertIsDisplayed()
    }

    @Composable
    private fun TestNavHost() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "intro") {
            composable("intro") { WizardPagerScreen(navController) }
            composable("main") {
                Box(
                    Modifier
                        .fillMaxSize()
                        .testTag("MainScreen")
                )
            }
        }
    }
}
