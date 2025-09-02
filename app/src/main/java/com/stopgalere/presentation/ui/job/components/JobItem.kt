package com.stopgalere.presentation.ui.job.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stopgalere.presentation.ui.job.JobUi

@Composable
fun JobItem(
    job: JobUi,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(12.dp),
    onClick: (JobUi) -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            // If you *must* hard-force pure white, swap to Color.White here.
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp, horizontal = 10.dp)
            .clickable { onClick(job) }
            .semantics { testTag = "JobCard_${job.title}" }
    ) {
        Box {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Spacer(Modifier.height(6.dp))

                Text(
                    job.title.uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(6.dp))

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        job.city,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        job.date,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic
                        ),
                        maxLines = 1,
                        textAlign = TextAlign.End,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Spacer(Modifier.height(6.dp))
            }
        }
    }
}

/* -------------------- Previews -------------------- */

private val previewJob = JobUi(
    id = "1", title = "Comptable Senior", city = "Abidjan",
    date = "28-08-2025",
    deadline = "28-09-2025",
    description = "Gestion comptable, rapports mensuels, etc.",
    sectorName = "Finances/Comptabilité", genderName = "Homme et femme",
    contractTypeName = "CDI", workModeName = "Plein temps",
    authorEmail = "secretaire@attractivbusinessforsign.net",
    authorWebsite = "www.cidj.com",
    authorMobile1 = "09632578\n22568963",
    authorMobile2 = "09632578\n22568963",
    authorLongitude = null, authorLatitude = null, company = "Attractiv Business",
    companyLogoUrl = "", salary = "450000", experience = "4 ans d'expérience(s)",
    educationLevel = "BAC+4, BAC+5, BAC+6, BAC+7"
)

@Preview(
    name = "Small Phone – Light",
    widthDp = 320, heightDp = 640,
    showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable
fun Preview_JobItem_Small_Light() {
    MaterialTheme {
        JobItem(job = previewJob)
    }
}

@Preview(
    name = "Medium Phone – Light",
    widthDp = 360, heightDp = 740,
    showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable
fun Preview_JobItem_Medium_Light() {
    MaterialTheme {
        JobItem(job = previewJob)
    }
}

@Preview(
    name = "Large Phone – Light",
    widthDp = 411, heightDp = 891,
    showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable
fun Preview_JobItem_Large_Light() {
    MaterialTheme {
        JobItem(job = previewJob)
    }
}

@Preview(
    name = "Tablet – Light",
    widthDp = 800, heightDp = 1280,
    showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable
fun Preview_JobItem_Tablet_Light() {
    MaterialTheme {
        JobItem(job = previewJob)
    }
}

@Preview(
    name = "Medium Phone – Dark",
    widthDp = 360, heightDp = 740,
    showBackground = true, backgroundColor = 0xFF000000
)
@Composable
fun Preview_JobItem_Medium_Dark() {
    // Using Material (M2) to match the composable’s typography usage.
    MaterialTheme {
        JobItem(job = previewJob)
    }
}
