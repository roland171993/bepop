package com.stopgalere.presentation.ui.job

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.stopgalere.presentation.ui.main.JobRow // reuse your existing row
import com.stopgalere.presentation.ui.main.components.NoContentPlaceholder

@Composable
fun JobListPaged(
    jobs: LazyPagingItems<JobUi>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 8.dp),
    onJobClick: (JobUi) -> Unit = {}
) {
    val itemCount = jobs.itemCount
    if (itemCount == 0 && jobs.loadState.refresh.endOfPaginationReached) {
        NoContentPlaceholder(
            modifier = modifier
                .fillMaxSize()
                .semantics { testTag = "EmptyJobs" }
        )
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
            .semantics { testTag = "JobList" }
            .navigationBarsPadding(),
        contentPadding = contentPadding
    ) {
        items(
            count = itemCount,
            key = jobs.itemKey { it.id }
        ) { index ->
            jobs[index]?.let { job ->
                androidx.compose.foundation.layout.Box(
                    Modifier.clickable { onJobClick(job) }
                ) { JobRow(job) }
            }
        }
        // You can append a footer/error/retry UI here using jobs.loadState
    }
}

/* ---------- Previews with fake paging ---------- */
@Preview(name = "JobList – Light", widthDp = 360, heightDp = 740, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable fun Preview_JobListPaged_Light() {
    // For previews, convert a static list to PagingData
    val fake = previewPagingItems(jobPreviewItems())
    MaterialTheme { JobListPaged(jobs = fake) }
}

@Preview(name = "JobList – Dark", widthDp = 360, heightDp = 740, showBackground = true)
@Composable fun Preview_JobListPaged_Dark() {
    val fake = previewPagingItems(jobPreviewItems())
    MaterialTheme { JobListPaged(jobs = fake) }
}

/* Utilities for previews */
import androidx.compose.runtime.remember
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.flowOf

private val LocalPreviewPaging = staticCompositionLocalOf { PagingData.empty<JobUi>() }

@Composable
private fun previewPagingItems(list: List<JobUi>): LazyPagingItems<JobUi> {
    val pd = remember { PagingData.from(list) }
    CompositionLocalProvider(LocalPreviewPaging provides pd) {
        return flowOf(pd).collectAsLazyPagingItems()
    }
}

private fun jobPreviewItems() = listOf(
    JobUi("1","Android Engineer","San Francisco","2025-08-01"),
    JobUi("2","Kotlin Dev","A very very long city name that will be ellipsized","2025-07-22"),
    JobUi("3","Compose Wizard","Paris","2025-07-01")
)
