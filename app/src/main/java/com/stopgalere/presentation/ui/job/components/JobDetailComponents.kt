package com.stopgalere.presentation.ui.job.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DetailSectionCard(
    title: String?,
    subtitle: String? = null,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope.() -> Unit)? = null
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            title?.let {
                Text(it, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = titleColor))
                Spacer(Modifier.height(6.dp))
            }
            subtitle?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            content?.let { Spacer(Modifier.height(8.dp)); it() }
        }
    }
}

@Composable
fun DetailChip(label: String, modifier: Modifier = Modifier) {
    AssistChip(onClick = {}, label = { Text(label) }, modifier = modifier)
}
