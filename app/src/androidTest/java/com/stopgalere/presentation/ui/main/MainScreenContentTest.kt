package com.stopgalere.presentation.ui.main

import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.testing.TestNavHostController
import androidx.paging.PagingData
import com.stopgalere.DeviceAnimationsRule
import com.stopgalere.MainActivity
import com.stopgalere.presentation.ui.job.JobUi
import com.stopgalere.presentation.viewmodel.MainViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

class MainScreenContentTest {

    // Prevent animations from keeping the main thread "busy/locked"
    @get:Rule(order = 0)
    val deviceAnimations = DeviceAnimationsRule()

    // ComposeCookBook-style: launch the real Activity
    @get:Rule(order = 1)
    val composeAndroidTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun main_showsData_whenJobsAvailable() {
        val vm: MainViewModel = mockk(relaxed = true)

        val items = listOf(
            JobUi("1", "Android Engineer", "Abidjan", "2025-08-01"),
            JobUi("2", "Kotlin Dev",       "Paris",   "2025-07-22")
        )

        every { vm.isSearchOpen } returns MutableStateFlow(false)
        every { vm.searchQuery }  returns MutableStateFlow("")
        every { vm.isOnline }     returns MutableStateFlow(true)
        every { vm.jobs }         returns fakeJobs(items)

        val nav = TestNavHostController(composeAndroidTestRule.activity)

        composeAndroidTestRule.activity.setContent {
            MaterialTheme { MainScreen(navController = nav, viewModel = vm) }
        }

        // Wait for a specific item text to appear
        composeAndroidTestRule.waitUntil(timeoutMillis = 10_000) {
            composeAndroidTestRule
                .onAllNodesWithText("ANDROID ENGINEER", useUnmergedTree = true) // useUnmergedTree might be needed
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeAndroidTestRule.onNodeWithTag("JobList").assertIsDisplayed()
        composeAndroidTestRule.onNodeWithText("ANDROID ENGINEER", useUnmergedTree = true).assertIsDisplayed()
        composeAndroidTestRule.onNodeWithText("KOTLIN DEV", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun main_showsEmpty_whenNoJobs() {
        val vm: MainViewModel = mockk(relaxed = true)

        every { vm.isSearchOpen } returns MutableStateFlow(false)
        every { vm.searchQuery }  returns MutableStateFlow("")
        every { vm.isOnline }     returns MutableStateFlow(true)
        every { vm.jobs }         returns fakeJobs(emptyList())

        val nav = TestNavHostController(composeAndroidTestRule.activity)

        composeAndroidTestRule.activity.setContent {
            MaterialTheme { MainScreen(navController = nav, viewModel = vm) }
        }

        composeAndroidTestRule.waitUntil(timeoutMillis = 10_000) {
            composeAndroidTestRule.onAllNodes(hasTestTag("JobList")).fetchSemanticsNodes().isNotEmpty() ||
                    composeAndroidTestRule.onAllNodes(hasTestTag("EmptyPlaceholder")).fetchSemanticsNodes().isNotEmpty()
        }

        // Correct tag comes from NoContentPlaceholder()
        composeAndroidTestRule.onNodeWithTag("EmptyPlaceholder").assertIsDisplayed()
    }

    private fun fakeJobs(items: List<JobUi>): Flow<PagingData<JobUi>> =
        flowOf(PagingData.from(items))
}
