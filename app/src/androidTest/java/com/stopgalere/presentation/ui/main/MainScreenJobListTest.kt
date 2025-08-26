package com.stopgalere.presentation.ui.main

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.*
import com.stopgalere.presentation.ui.job.JobUi
import com.stopgalere.presentation.ui.main.components.MainScreenContent
import com.stopgalere.utils.fakePagingItems
import org.junit.Rule
import org.junit.Test

class MainScreenJobListTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun jobList_showsFakeItems() {
        val fakeJobs = listOf(
            JobUi("1", "Android Engineer", "Abidjan", "2025-08-01"),
            JobUi("2", "Kotlin Dev", "Paris",   "2025-07-22"),
            JobUi("3", "Compose Wizard", "Lyon","2025-07-01")
        )

        rule.setContent {
            val items = fakePagingItems(fakeJobs)
            MainScreenContent(
                isDrawerOpen = false,
                isSearchOpen = false,
                query = "",
                isOnline = true,
                onQueryChange = {},
                onNavClick = {},
                onSetSearchActive = {},
                jobs = items,
                onRefresh = {},
                listState = LazyListState()
            )
        }

        // Wait until the loading indicator is NOT present
        // and the JobList IS present.
        rule.waitUntil(timeoutMillis = 5_000) {
            // Check that JobsLoading is gone OR JobList is present
            // It's better to wait for JobList to be present directly.
            rule.onAllNodesWithTag("JobList").fetchSemanticsNodes().isNotEmpty()
        }

        // Now assert the list and its items
        rule.onNodeWithTag("JobList").assertExists().assertIsDisplayed()
        rule.onNodeWithText("ANDROID ENGINEER").assertIsDisplayed()
        rule.onNodeWithText("KOTLIN DEV").assertIsDisplayed()
        rule.onNodeWithText("COMPOSE WIZARD").assertIsDisplayed()
    }

    @Test
    fun jobList_showsEmptyPlaceholder_whenNoItems() {
        rule.setContent {
            val items = fakePagingItems(emptyList<JobUi>())
            MainScreenContent(
                isDrawerOpen = false,
                isSearchOpen = false,
                query = "",
                isOnline = true,
                onQueryChange = {},
                onNavClick = {},
                onSetSearchActive = {},
                jobs = items,
                onRefresh = {},
                listState = LazyListState()
            )
        }

        // Tag from your NoContentPlaceholder branch
        rule.onNodeWithTag("EmptyJobs").assertExists().assertIsDisplayed()
    }
}
