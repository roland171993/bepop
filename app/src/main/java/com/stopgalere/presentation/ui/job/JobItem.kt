package com.stopgalere.presentation.ui.job

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * Pure UI (stateless) item for a Job.
 * Accepts the UI model (JobUi) defined in the presentation layer.
 */
@Composable
fun JobItem(
    job: JobUi,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(12.dp),
    onClick: (JobUi) -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(job) },
        elevation = 0.dp
    ) {
        Column(Modifier.padding(contentPadding)) {
            Text(
                text = job.title,
                style = MaterialTheme.typography.subtitle1,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.padding(top = 4.dp))
            Text(
                text = job.city,
                style = MaterialTheme.typography.body2
            )
            Spacer(Modifier.padding(top = 2.dp))
            Text(
                text = job.date,
                style = MaterialTheme.typography.caption
            )
        }
    }
    Divider()
}
