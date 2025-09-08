package com.stopgalere.presentation.ui.job

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.RolandAssoh.stopgalere.ci.R
import com.stopgalere.presentation.theme.Gray101
import com.stopgalere.presentation.ui.job.components.JobItem
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
            Box(modifier = Modifier
                .fillMaxSize()
                .semantics { testTag = "JobsLoading" },
                contentAlignment = Alignment.Center){
                CircularProgressIndicator(
                    modifier = Modifier.padding(24.dp),
                    color = MaterialTheme.colorScheme.primary)
            }

        }
        jobs.loadState.refresh is LoadState.Error -> {
            val e = jobs.loadState.refresh as LoadState.Error
            Box(modifier = Modifier.fillMaxSize(),
                Alignment.Center){
                Text("Erreur: ${e.error.message ?: "unknown"}", color = Color.Red, modifier = Modifier.padding(16.dp))
            }
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
                .background(color = Gray101)
                .semantics { testTag = "JobList" }
                .navigationBarsPadding(),
                contentPadding = contentPadding,
                state = listState) {
                items(count = jobs.itemCount,
                    key = { index -> jobs.peek(index)?.id ?: index }) { index ->
                    jobs[index]?.let { job ->
                        JobItem(
                            job = job,
                            onClick = onJobClick
                        )
                    }
                }
                // show append state
                item {
                    when (jobs.loadState.append) {
                        is LoadState.Loading -> LinearProgressIndicator(Modifier.fillMaxWidth())
                        is LoadState.Error -> Text(stringResource(R.string.job_list_no_more), modifier = Modifier.padding(16.dp))
                        else -> {}
                    }
                }
            }
        }
    }
}