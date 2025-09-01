package com.stopgalere.presentation.ui.job.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stopgalere.presentation.theme.StopGalereTheme
import com.stopgalere.presentation.ui.job.JobDetailContent
import com.stopgalere.presentation.ui.job.JobDetailPreviewData

@Composable
fun DetailSectionCard(
    backgroudColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = backgroudColor),
        modifier = modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        content()
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
fun DetailChip(label: String, modifier: Modifier = Modifier) {
    AssistChip(onClick = {}, label = { Text(label) }, modifier = modifier)
}
