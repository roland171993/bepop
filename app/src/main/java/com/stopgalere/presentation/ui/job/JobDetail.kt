package com.stopgalere.presentation.ui.job

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import com.stopgalere.presentation.theme.Orange
import com.stopgalere.presentation.theme.StopGalereTheme
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.stopgalere.presentation.theme.Green
import compose.icons.fontawesomeicons.solid.Building
import androidx.core.net.toUri
import compose.icons.fontawesomeicons.solid.ArrowLeft
import compose.icons.fontawesomeicons.solid.Backward
import compose.icons.fontawesomeicons.solid.PhoneAlt


@Composable
fun JobDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: JobDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    when (val s = uiState) {
        is JobDetailUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
        is JobDetailUiState.Error   -> Box(Modifier.fillMaxSize(), Alignment.Center) { Text(s.message ?: "Unable to load job", color = MaterialTheme.colorScheme.error) }
        is JobDetailUiState.Success -> JobDetailContent(job = s.job, isOnline = isOnline, modifier = modifier)
    }
}

/** Stateless content → great for previews and UI tests. */
@Composable
fun JobDetailContent(
    job: JobUi,
    isOnline: Boolean,
    modifier: Modifier = Modifier,
) {
    val ctx = LocalContext.current

    // Default platform actions (still overridable for testing)
    val doWeb = remember(job.authorWebsite) {
        { url: String ->
            if (url.isNotBlank()) {
                val safe = if (url.startsWith("http")) url else "https://$url"
                ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(safe)))
            }
        }
    }

    val doCall1 = remember(job.authorMobile1) {
        { phone: String ->
            if (phone.isNotBlank()) ctx.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
        }
    }
    val doCall2 = remember(job.authorMobile2) {
        { phone: String ->
            if (phone.isNotBlank()) ctx.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
        }
    }
    val doMail = remember(job.authorEmail) {
        { email: String ->
            if (email.isNotBlank()) ctx.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email")))
        }
    }

    val doShare = remember(job) {
        {
            val txt = buildString {
                appendLine(job.title)
                appendLine(job.company )
                appendLine(job.city )
                appendLine(job.description)
            }
            ctx.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"; putExtra(Intent.EXTRA_TEXT, txt)
            }, "Share"))
        }
    }

    val topBarBg = MaterialTheme.colorScheme.secondary
    val onTopBar = MaterialTheme.colorScheme.onSecondary
    val iconSize = 20.dp

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background)
            .semantics { testTag = "JobDetail" },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top action icons
        Row(
            Modifier
                .fillMaxWidth()
                .background(topBarBg),
            horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { doCall1(job.authorMobile1) }, content = {
                Icon(FontAwesomeIcons.Solid.ArrowLeft, contentDescription = "Call1", tint = onTopBar, modifier = modifier.size(iconSize))
            })
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(topBarBg)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(onClick = { doCall1(job.authorMobile1) }, content = {
                    Icon(FontAwesomeIcons.Solid.PhoneAlt, contentDescription = "Call1", tint = onTopBar, modifier = modifier.size(iconSize))
                })
                IconButton(onClick = { doCall2(job.authorMobile2) }, content = {
                    Icon(FontAwesomeIcons.Solid.Phone, contentDescription = "Call2", tint = onTopBar, modifier = modifier.size(iconSize))
                })
                IconButton(onClick = { doMail(job.authorEmail) },
                    content = {
                        Icon(FontAwesomeIcons.Solid.PaperPlane, contentDescription = "Send", tint = onTopBar, modifier = modifier.size(iconSize))
                    })
                if (job.authorWebsite.isNotEmpty()) {
                    IconButton(onClick = { doWeb(job.authorWebsite) },
                        content = {
                            Icon(FontAwesomeIcons.Solid.Globe, contentDescription = "Website", tint = onTopBar, modifier = modifier.size(iconSize))
                        })
                }

                IconButton(onClick = {
                    doShare() },
                    content = {
                        Icon(FontAwesomeIcons.Solid.Share, contentDescription = "Share", tint = onTopBar, modifier = modifier.size(iconSize))
                    })
            }
        }


        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .semantics { testTag = "JobDetail" },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(job.title, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary), maxLines = 2, overflow = TextOverflow.Ellipsis)

            HeaderItem("DESCRIPTION")
            BodyItem(job.description)

            HeaderItem("EMPLOYEUR")
            BodyItem(job.company )
            CompanyLogo(url = job.companyLogoUrl, isOnline = isOnline)

            HeaderItem("PUBLICATION")
            BodyItem(job.date )

            HeaderItem("DATE LIMITE")
            Text(job.deadline, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = Color.Red)

            if (job.authorMobile1.isNotEmpty() or job.authorMobile2.isNotEmpty()) {
                HeaderItem("CONTACT(S)")
                val phone =  job.authorMobile1 + "\n" + job.authorMobile2
                Text(phone, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            HeaderItem("SECTEUR D'ACTIVITÉ")
            BodyItem(job.sectorName )

            if (job.authorWebsite.isNotEmpty()) {
                HeaderItem("SITE WEB")
                BodyItem(job.authorWebsite )
            }

            if (job.authorEmail.isNotEmpty()) {
                HeaderItem("EMAIL")
                BodyItem(job.authorEmail )
            }

            // Salary strip
            DetailSectionCard(backgroudColor = Orange, modifier = modifier.height(200.dp)) {
                Row(Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(job.salary, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold), color = Color.White)
                    Spacer(Modifier.width(12.dp))
                    Icon(FontAwesomeIcons.Solid.PiggyBank,
                        contentDescription = null,
                        modifier = modifier
                            .size(59.dp),
                        tint = Color.White)
                }
            }

            // Work details (contract / mode / city)
            DetailSectionCard(backgroudColor = Color.White, modifier = modifier.height(200.dp)) {
                Row(Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(FontAwesomeIcons.Solid.Briefcase,
                        contentDescription = null,
                        modifier = Modifier.size(49.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        DetailChip(label = job.contractTypeName ,isOnline = isOnline, isCity = false)
                        DetailChip(label = job.workModeName,isOnline = isOnline, isCity = false)
                        DetailChip(label = job.city,isOnline = isOnline, isCity = true)
                    }
                }
            }

            // Requirements strip
            DetailSectionCard (backgroudColor = Green, modifier = modifier.height(200.dp)){
                Column(Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp, bottom = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center) {
                    Icon(FontAwesomeIcons.Solid.Search, contentDescription = null, modifier = Modifier.size(49.dp), tint = Color.White)
                    DetailItem(job.genderName)
                    DetailItem(job.educationLevel)
                    DetailItem(job.experience)
                }
            }
            Spacer(Modifier.height(50.dp)) // avoid cutoff on real device cause Scafold not used we want from scratch
        }


    }
}


@Composable
fun CompanyLogo( modifier: Modifier = Modifier, url: String, isOnline: Boolean) {
    if (url.isNotEmpty() and url.contains("http") and isOnline) {
        AsyncImage(
            model = url,
            contentDescription = "Company Logo",
            modifier = modifier.size(80.dp)
        )
    }
}


@Composable
private fun HeaderItem(text: String){
    Spacer(Modifier.height(12.dp))
    Text(text, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.secondary)
}

@Composable
private fun  BodyItem(text: String){
    Text(text, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(Modifier.height(12.dp))
}

/* Small building block used above */
@Composable
private fun DetailItem(text: String) {
    Spacer(Modifier.height(4.dp))
    Text(text, style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),maxLines = 2, overflow = TextOverflow.Ellipsis)
}

object JobDetailPreviewData {
    val job = com.stopgalere.domain.model.Job(
        id = "1", title = "Comptable Senior", city = "Abidjan",
        date = "28-08-2025", dateAdded = "2025-08-28",
        deadline = "28-09-2025",
        description = "Gestion comptable, rapports mensuels, etc.",
        sectorName = "Finances/Comptabilité", genderName = "Homme et femme",
        contractTypeName = "CDI", workModeName = "Plein temps",
        authorEmail = "secretaire@attractivbusinessforsign.net",
        authorWebsite = "www.cidj.com", authorMobile1 = "09632578\n22568963",authorMobile2 = "03632378\n775665963",
        authorLongitude = null, authorLatitude = null, company = "Attractiv Business",
        companyLogoUrl = "", salary = "450000", experience = "4 ans d'expérience(s)",
        educationLevel = "BAC+4, BAC+5, BAC+6, BAC+7"
    ).toUi()
}

@Preview(name = "Small – Light",  widthDp = 320, heightDp = 640, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable fun Preview_JobDetail_Small_Light()  { StopGalereTheme (dynamicColor = true) { JobDetailContent(job = JobDetailPreviewData.job, isOnline = false) } }
@Preview(name = "Medium – Light", widthDp = 360, heightDp = 740, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable fun Preview_JobDetail_Medium_Light() { StopGalereTheme(dynamicColor = false) { JobDetailContent(job = JobDetailPreviewData.job, isOnline = false) } }
@Preview(name = "Large – Light",  widthDp = 411, heightDp = 891, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable fun Preview_JobDetail_Large_Light()  { StopGalereTheme { JobDetailContent(job = JobDetailPreviewData.job, isOnline = false) } }
@Preview(name = "Tablet – Light", widthDp = 800, heightDp = 1280, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable fun Preview_JobDetail_Tablet_Light() { StopGalereTheme { JobDetailContent(job = JobDetailPreviewData.job, isOnline = false) } }
@Preview(name = "Medium – Dark",  widthDp = 360, heightDp = 740, showBackground = true, backgroundColor = 0xFF000000)
@Composable fun Preview_JobDetail_Medium_Dark()  { StopGalereTheme { JobDetailContent(job = JobDetailPreviewData.job, isOnline = false) } }
