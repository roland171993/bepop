package com.stopgalere.presentation.ui.job

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stopgalere.presentation.ui.main.JobRow
import com.stopgalere.presentation.ui.main.JobUi
import com.stopgalere.presentation.ui.main.sampleJobs
import com.stopgalere.presentation.ui.main.components.NoContentPlaceholder

/**
 * Stateless list that displays jobs using JobRow.
 * - Ready for UI testing (has semantically tagged root).
 * - Ready for mocking (pure data-in, UI-out).
 */
@Composable
fun JobList(
    jobs: List<JobUi>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 8.dp),
    listState: LazyListState = rememberLazyListState(),
    onJobClick: (JobUi) -> Unit = {} // keeps the row reusable for navigation/detail later
) {
    if (jobs.isEmpty()) {
        NoContentPlaceholder(
            modifier = modifier
                .fillMaxSize()
                .semantics { testTag = "EmptyJobs" }
        )
        return
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
            .semantics { testTag = "JobList" }
            .navigationBarsPadding(),
        contentPadding = contentPadding
    ) {
        // Use a stable-ish key to help recycling & animations
        items(
            items = jobs,
            key = { "${it.title}_${it.city}_${it.date}" }
        ) { job ->
            // Click support for future navigation (kept optional)
            androidx.compose.foundation.layout.Box(
                Modifier.clickable { onJobClick(job) }
            ) {
                JobRow(job)
            }
        }
    }
}

/* ---------- Previews (detached from DI/Nav) ---------- */

@Preview(name = "Small – light", widthDp = 320, heightDp = 640, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable fun Preview_JobList_Small() { MaterialTheme { JobList(jobs = sampleJobs()) } }

@Preview(name = "Medium – light", widthDp = 360, heightDp = 740, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable fun Preview_JobList_Medium() { MaterialTheme { JobList(jobs = sampleJobs()) } }

@Preview(name = "Tall phone – light", widthDp = 411, heightDp = 891, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable fun Preview_JobList_Tall() { MaterialTheme { JobList(jobs = sampleJobs()) } }

@Preview(name = "Tablet – light", widthDp = 800, heightDp = 1280, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable fun Preview_JobList_Tablet() { MaterialTheme { JobList(jobs = sampleJobs()) } }

@Preview(name = "Small – dark", widthDp = 320, heightDp = 640, showBackground = true)
@Composable fun Preview_JobList_Small_Dark() { MaterialTheme { JobList(jobs = sampleJobs()) } }
