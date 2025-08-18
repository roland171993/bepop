package com.stopgalere.presentation.ui.main


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import com.stopgalere.presentation.ui.main.components.NoContentPlaceholder
import com.stopgalere.presentation.ui.job.JobList

@Composable
fun MainList(
    jobs: List<JobUi>,
    modifier: Modifier = Modifier
) {
    if (jobs.isEmpty()) {
        NoContentPlaceholder(modifier = Modifier.fillMaxSize())
    } else {
        JobList(
            jobs = jobs,
            modifier = modifier.fillMaxSize()
            // onJobClick = { /* navigate when ready */ }
        )
    }
}