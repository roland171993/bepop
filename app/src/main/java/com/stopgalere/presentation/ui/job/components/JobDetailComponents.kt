package com.stopgalere.presentation.ui.job.components

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import androidx.core.net.toUri

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
fun DetailChip(
    modifier: Modifier = Modifier,
    label: String,
    isOnline: Boolean,
    isCity: Boolean = false
) {
    val context = LocalContext.current
    AssistChip(
        onClick = {
            if (isOnline) {
                val intent = if (isCity) {
                    // Open city in Google Maps
                    Intent(
                        Intent.ACTION_VIEW,
                        "https://www.google.com/maps/search/?api=1&query=${Uri.encode(label)}".toUri()
                    )
                } else {
                    // Default: open Google search for definition
                    Intent(
                        Intent.ACTION_VIEW,
                        ("https://www.google.com/search?q=définir+travail+" + Uri.encode(label)).toUri()
                    )
                }
                context.startActivity(intent)
            }
        },
        label = { Text(text = label, maxLines = 2, overflow = TextOverflow.Ellipsis) },
        modifier = modifier.semantics { testTag = "JobDetail_Chip_$label" },
        enabled = isOnline
    )
}
