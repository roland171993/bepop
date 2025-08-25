package com.stopgalere.presentation.ui.main

import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.testing.TestNavHostController
import androidx.paging.PagingData
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

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun mainScreen_showsFakeJobs() {
        // Mock the Hilt VM and stub its public flows
        val vm = mockk<MainViewModel>(relaxed = true)

        every { vm.isSearchOpen } returns MutableStateFlow(false)
        every { vm.searchQuery }  returns MutableStateFlow("")
        every { vm.isOnline }     returns MutableStateFlow(true)
        every { vm.jobs } returns fakeJobsFlow(
            listOf(
                JobUi("1", "Android Engineer", "Abidjan", "2025-08-01"),
                JobUi("2", "Kotlin Dev",       "Paris",   "2025-07-22")
            )
        )

        val nav = TestNavHostController(composeRule.activity)

        composeRule.activity.setContent {
            MaterialTheme {
                MainScreen(navController = nav, viewModel = vm)
            }
        }

        // Wait for the list to render (Paging emits asynchronously)
        composeRule.waitUntil(timeoutMillis = 10_000) {
            composeRule.onAllNodes(hasTestTag("JobList")).fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithTag("JobList").assertIsDisplayed()
        // Titles are uppercased in JobItem
        composeRule.onNodeWithText("ANDROID ENGINEER").assertIsDisplayed()
        composeRule.onNodeWithText("KOTLIN DEV").assertIsDisplayed()
    }

    @Test
    fun mainScreen_showsEmptyPlaceholder_whenNoJobs() {
        val vm = mockk<MainViewModel>(relaxed = true)

        every { vm.isSearchOpen } returns MutableStateFlow(false)
        every { vm.searchQuery }  returns MutableStateFlow("")
        every { vm.isOnline }     returns MutableStateFlow(true)
        every { vm.jobs } returns fakeJobsFlow(emptyList())

        val nav = TestNavHostController(composeRule.activity)

        composeRule.activity.setContent {
            MaterialTheme {
                MainScreen(navController = nav, viewModel = vm)
            }
        }

        // Wait until either list or empty state appears, then verify empty
        composeRule.waitUntil(timeoutMillis = 10_000) {
            val hasList  = composeRule.onAllNodes(hasTestTag("JobList")).fetchSemanticsNodes().isNotEmpty()
            val hasEmpty = composeRule.onAllNodes(hasTestTag("EmptyJobs")).fetchSemanticsNodes().isNotEmpty()
            hasList || hasEmpty
        }

        composeRule.onNodeWithTag("EmptyJobs").assertIsDisplayed()
    }

    private fun fakeJobsFlow(items: List<JobUi>): Flow<PagingData<JobUi>> =
        flowOf(PagingData.from(items))
}
