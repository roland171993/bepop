package com.stopgalere.presentation.ui.job

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.stopgalere.domain.model.Job
import com.stopgalere.presentation.ui.job.components.DetailChip
import com.stopgalere.presentation.ui.job.components.DetailSectionCard
import com.stopgalere.presentation.viewmodel.JobDetailUiState
import com.stopgalere.presentation.viewmodel.JobDetailViewModel
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Briefcase
import compose.icons.fontawesomeicons.solid.Envelope
import compose.icons.fontawesomeicons.solid.Globe
import compose.icons.fontawesomeicons.solid.PaperPlane
import compose.icons.fontawesomeicons.solid.Phone
import compose.icons.fontawesomeicons.solid.PiggyBank
import compose.icons.fontawesomeicons.solid.Search
import compose.icons.fontawesomeicons.solid.Share

@Composable
fun JobDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: JobDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    when (val s = uiState) {
        is JobDetailUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
        is JobDetailUiState.Error   -> Box(Modifier.fillMaxSize(), Alignment.Center) { Text(s.message ?: "Unable to load job", color = MaterialTheme.colorScheme.error) }
        is JobDetailUiState.Success -> JobDetailContent(job = s.job, modifier = modifier)
    }
}

/** Stateless content → great for previews and UI tests. */
@Composable
fun JobDetailContent(
    job: JobUi,
    modifier: Modifier = Modifier,
    onCall: (String) -> Unit = {},
    onMail: (String) -> Unit = {},
    onWeb: (String) -> Unit = {},
    onShare: (String) -> Unit = {},
) {
    val ctx = LocalContext.current

    // Default platform actions (still overridable for testing)
    val doCall = remember(job.authorMobile1) {
        { phone: String ->
            if (phone.isNotBlank()) ctx.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
        }
    }
    val doMail = remember(job.authorEmail) {
        { email: String ->
            if (email.isNotBlank()) ctx.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email")))
        }
    }
    val doWeb = remember(job.authorWebsite) {
        { url: String ->
            if (url.isNotBlank()) {
                val safe = if (url.startsWith("http")) url else "https://$url"
                ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(safe)))
            }
        }
    }
    val doShare = remember(job) {
        {
            val txt = buildString {
                appendLine(job.title)
                appendLine(job.company ?: "")
                appendLine(job.city ?: "")
                appendLine(job.description ?: "")
            }
            ctx.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"; putExtra(Intent.EXTRA_TEXT, txt)
            }, "Share"))
        }
    }

    val topBarBg = MaterialTheme.colorScheme.primary
    val onTopBar = MaterialTheme.colorScheme.onPrimary

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .semantics { testTag = "JobDetail" },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top action icons
        Row(
            Modifier.fillMaxWidth().background(topBarBg).padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton({ (job.authorMobile1 ?: "").also { onCall(it); doCall(it) } }) { Icon(FontAwesomeIcons.Solid.Phone, contentDescription = "Call", tint = onTopBar) }
            IconButton({ (job.authorMobile1 ?: "").also { onCall(it); doCall(it) } }) { Icon(FontAwesomeIcons.Solid.Phone, contentDescription = "Phone", tint = onTopBar) }
            IconButton({ doShare(); onShare(job.title) }) { Icon(FontAwesomeIcons.Solid.PaperPlane, contentDescription = "Send", tint = onTopBar) }
            IconButton({ (job.authorWebsite ?: "").also { onWeb(it); doWeb(it) } }) { Icon(FontAwesomeIcons.Solid.Globe, contentDescription = "Website", tint = onTopBar) }
            IconButton({ doShare(); onShare(job.title) }) { Icon(FontAwesomeIcons.Solid.Share, contentDescription = "Share", tint = onTopBar) }
        }

        Text(job.title, style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold), maxLines = 2, overflow = TextOverflow.Ellipsis)

        Text("DESCRIPTION", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
        Text(job.description, style = MaterialTheme.typography.bodyLarge )

        // Header / title
        Column(Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {

            val subtitle = listOfNotNull(job.company, job.city, job.date).filter { it.isNotBlank() }.joinToString(" • ")
            if (subtitle.isNotBlank()) Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){

        }



        // Contacts
        DetailSectionCard(title = "CONTACT(S)", titleColor = MaterialTheme.colorScheme.primary) {
            val phone = job.authorMobile1 ?: "—"
            Text(phone, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black), modifier = Modifier.align(Alignment.CenterHorizontally))
            if (!phone.contains("\n")) Spacer(Modifier.height(6.dp))
            job.authorEmail?.takeIf { it.isNotBlank() }?.let {
                DetailRow(FontAwesomeIcons.Solid.Envelope, it) { onMail(it); doMail(it) }
            }
            job.authorWebsite?.takeIf { it.isNotBlank() }?.let {
                DetailRow(FontAwesomeIcons.Solid.Globe, it) { onWeb(it); doWeb(it) }
            }
        }

        // Sector
        DetailSectionCard(title = "SECTEUR D'ACTIVITÉ", subtitle = job.sectorName ?: "—", titleColor = MaterialTheme.colorScheme.primary)

        // Salary strip
        Surface(Modifier.fillMaxWidth().padding(top = 8.dp), color = Color(0xFFDB7A3D), contentColor = Color.White, shadowElevation = 1.dp) {
            Row(Modifier.fillMaxWidth().padding(16.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                val salaryText = job.salary?.let { String.format("%,d FCFA", it).replace(',', ' ') } ?: "Salaire non renseigné"
                Text(salaryText, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold))
                Icon(FontAwesomeIcons.Solid.PiggyBank, contentDescription = null)
            }
        }

        // Work details (contract / mode / city)
        DetailSectionCard(title = null) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(FontAwesomeIcons.Solid.Briefcase, contentDescription = null, modifier = Modifier.size(48.dp))
                Spacer(Modifier.width(12.dp))
                Column {
                    DetailChip(job.contractTypeName ?: "—"); Spacer(Modifier.height(4.dp))
                    DetailChip(job.workModeName     ?: "—"); Spacer(Modifier.height(4.dp))
                    DetailChip(job.city             ?: "—")
                }
            }
        }

        // Requirements strip
        Surface(Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 24.dp), color = Color(0xFF4CAF50), contentColor = Color.White) {
            Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(FontAwesomeIcons.Solid.Search, contentDescription = null, modifier = Modifier.size(64.dp))
                Spacer(Modifier.height(8.dp))
                Text(job.genderName     ?: "—", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(job.educationLevel ?: "—", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(4.dp))
                Text(job.experience     ?: "—", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

/* Small building block used above */
@Composable
private fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: (() -> Unit)? = null) {
    val row: @Composable () -> Unit = {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null); Spacer(Modifier.width(8.dp)); Text(label, style = MaterialTheme.typography.bodyLarge)
        }
    }
    Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp)
            .then(if (onClick != null) Modifier.semantics { testTag = "DetailRow_$label" } else Modifier),
        verticalAlignment = Alignment.CenterVertically
    ) { row() }
}

object JobDetailPreviewData {
    val job = com.stopgalere.domain.model.Job(
        id = "1", title = "Comptable Senior", city = "Abidjan",
        date = "28-08-2025", dateAdded = "2025-08-28",
        description = "Gestion comptable, rapports mensuels, etc.",
        sectorName = "Finances/Comptabilité", genderName = "Homme et femme",
        contractTypeName = "CDI", workModeName = "Plein temps",
        authorEmail = "secretaire@attractivbusinessforsign.net",
        authorWebsite = "www.cidj.com", authorMobile1 = "09632578\n22568963",
        authorLongitude = null, authorLatitude = null, company = "Attractiv Business",
        companyLogoUrl = "", salary = 450000, experience = "4 ans d'expérience(s)",
        educationLevel = "BAC+4, BAC+5, BAC+6, BAC+7"
    ).toUi()
}

@Preview(name = "Small – Light",  widthDp = 320, heightDp = 640, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable fun Preview_JobDetail_Small_Light()  { MaterialTheme { JobDetailContent(job = JobDetailPreviewData.job) } }
@Preview(name = "Medium – Light", widthDp = 360, heightDp = 740, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable fun Preview_JobDetail_Medium_Light() { MaterialTheme { JobDetailContent(job = JobDetailPreviewData.job) } }
@Preview(name = "Large – Light",  widthDp = 411, heightDp = 891, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable fun Preview_JobDetail_Large_Light()  { MaterialTheme { JobDetailContent(job = JobDetailPreviewData.job) } }
@Preview(name = "Tablet – Light", widthDp = 800, heightDp = 1280, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable fun Preview_JobDetail_Tablet_Light() { MaterialTheme { JobDetailContent(job = JobDetailPreviewData.job) } }
@Preview(name = "Medium – Dark",  widthDp = 360, heightDp = 740, showBackground = true, backgroundColor = 0xFF000000)
@Composable fun Preview_JobDetail_Medium_Dark()  { MaterialTheme { JobDetailContent(job = JobDetailPreviewData.job) } }
