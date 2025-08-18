package com.stopgalere.presentation.ui.job

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.stopgalere.presentation.ui.main.components.NoContentPlaceholder

/**
 * Stateless list that displays jobs using JobRow.
 * - Ready for UI testing (has semantically tagged root).
 * - Ready for mocking (pure data-in, UI-out).
 */
@Composable
fun JobList(
    jobs: LazyPagingItems<JobUi>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(vertical = 8.dp),
    listState: LazyListState = rememberLazyListState(),
    onJobClick: (JobUi) -> Unit = {} // keeps the row reusable for navigation/detail later
) {
    when {
        jobs.loadState.refresh is LoadState.Loading -> {
            // initial skeleton
            CircularProgressIndicator(modifier = Modifier.padding(24.dp))
        }
        jobs.loadState.refresh is LoadState.Error -> {
            val e = jobs.loadState.refresh as LoadState.Error
            Text("Error: ${e.error.message ?: "unknown"}", color = Color.Red, modifier = Modifier.padding(16.dp))
        }
        jobs.itemCount == 0 -> {
            NoContentPlaceholder(
                modifier = Modifier
                    .fillMaxSize().
                    semantics { testTag = "EmptyJobs" })
        }
        else -> {
            LazyColumn(modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF2F2F2))
                .semantics { testTag = "JobList" }
                .navigationBarsPadding(),
                contentPadding = contentPadding) {
                items(jobs.itemCount) { index ->
                    jobs[index]?.let { job ->
                        androidx.compose.foundation.layout.Box(
                            Modifier.clickable { onJobClick(job) }
                        ) {
                            JobRow(job)
                        }
                    }
                }
                // show append state
                item {
                    when (jobs.loadState.append) {
                        is LoadState.Loading -> LinearProgressIndicator(Modifier.fillMaxWidth())
                        is LoadState.Error -> Text("Couldn’t load more.", modifier = Modifier.padding(16.dp))
                        else -> {}
                    }
                }
            }
        }
    }
}