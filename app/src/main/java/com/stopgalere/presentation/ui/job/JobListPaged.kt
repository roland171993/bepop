package com.stopgalere.presentation.ui.job

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.stopgalere.presentation.ui.job.components.JobItem
import com.stopgalere.presentation.ui.main.components.NoContentPlaceholder
import kotlinx.coroutines.flow.flowOf

@Composable
fun JobListPaged(
    jobs: LazyPagingItems<JobUi>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 8.dp),
    listState: LazyListState = rememberLazyListState(),
    onJobClick: (JobUi) -> Unit = {}
) {
    if (jobs.itemCount == 0 && jobs.loadState.refresh is LoadState.NotLoading) {
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
        state = listState,
        contentPadding = contentPadding
    ) {
        items(
            count = jobs.itemCount,
            key = { index -> jobs.peek(index)?.id ?: "placeholder-$index" }
        ) { index ->
            jobs[index]?.let { job ->
                JobItem(
                    job = job,
                    onClick = onJobClick
                )
            }
        }

        item {
            when (val s = jobs.loadState.append) {
                is LoadState.Loading -> LinearProgressIndicator(Modifier.fillMaxWidth())
                is LoadState.Error -> Text(
                    text = "Couldn’t load more: ${s.error.message.orEmpty()}",
                    modifier = Modifier.fillMaxWidth()
                )
                else -> {}
            }
        }
    }
}

/* ---------- Previews with fake paging ---------- */

@Preview(
    name = "JobList – Light",
    widthDp = 360, heightDp = 740,
    showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable
fun Preview_JobListPaged_Light() {
    val fake = previewPagingItems(jobPreviewItems())
    MaterialTheme { JobListPaged(jobs = fake) }
}

@Preview(
    name = "JobList – Dark",
    widthDp = 360, heightDp = 740,
    showBackground = true
)
@Composable
fun Preview_JobListPaged_Dark() {
    val fake = previewPagingItems(jobPreviewItems())
    MaterialTheme { JobListPaged(jobs = fake) }
}

/* ---------- Utilities for previews ---------- */

@Composable
private fun previewPagingItems(list: List<JobUi>): LazyPagingItems<JobUi> {
    val pd = remember { PagingData.from(list) }
    return flowOf(pd).collectAsLazyPagingItems()
}

private fun jobPreviewItems() = listOf(
    JobUi("1", "Android Engineer", "San Francisco", "2025-08-01"),
    JobUi("2", "Kotlin Dev", "A very very long city name that will be ellipsized", "2025-07-22"),
    JobUi("3", "Compose Wizard", "Paris", "2025-07-01")
)
