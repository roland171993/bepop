@file:OptIn(ExperimentalFoundationApi::class)

package com.stopgalere.ui.presentation.intro

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

@OptIn(ExperimentalFoundationApi::class)
class WizardPagerStateTest {
    private val dispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial page is zero`() = runTest {
        // use the public factory function, not the internal ctor
        val state = PagerState { 3 }
        assertEquals(0, state.currentPage)
    }

    @Test
    fun `scrollToPage moves to specified page`() = runTest {
        val state = PagerState { 3 }
        state.scrollToPage(2)
        assertEquals(2, state.currentPage)
    }

    @Test
    fun `animateScrollToPage animates to next page`() = runTest {
        val state = PagerState { 3 }
        val job = launch { state.animateScrollToPage(1) }
        advanceUntilIdle()
        job.join()
        assertEquals(1, state.currentPage)
    }
}
