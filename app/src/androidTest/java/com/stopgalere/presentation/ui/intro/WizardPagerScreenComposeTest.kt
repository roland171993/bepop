package com.RolandAssoh.stopgalere.ci

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.stopgalere.presentation.theme.StopGalereTheme
import com.stopgalere.presentation.ui.intro.WizardPagerScreen
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WizardPagerScreenComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: TestNavHostController

    @Before
    fun setup() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        navController = TestNavHostController(ctx).apply {
            navigatorProvider.addNavigator(ComposeNavigator())
        }
    }

    @Test
    fun pager_displaysPages_and_navigatesOnFinish() {
        composeTestRule.setContent {
            StopGalereTheme {
                WizardPagerScreen(navController)
            }
        }

        // Page 0 visible
        composeTestRule.onNodeWithTag("WizardPage_0").assertExists()
        // Click “Next” → Page 1
        composeTestRule.onNodeWithTag("WizardNextButton").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("WizardPage_1").assertExists()

        // Click “Next” → Page 2
        composeTestRule.onNodeWithTag("WizardNextButton").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("WizardPage_2").assertExists()

        // Click “Finish” → should navigate to “main”
        composeTestRule.onNodeWithTag("WizardNextButton").performClick()
        composeTestRule.waitForIdle()
        Assert.assertEquals("main", navController.currentBackStackEntry?.destination?.route)
    }
}