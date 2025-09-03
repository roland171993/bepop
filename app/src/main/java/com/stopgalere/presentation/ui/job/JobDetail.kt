package com.stopgalere.presentation.ui.job

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.stopgalere.presentation.theme.Green
import com.stopgalere.presentation.theme.Orange
import com.stopgalere.presentation.ui.job.components.DetailChip
import com.stopgalere.presentation.ui.job.components.DetailSectionCard
import com.stopgalere.presentation.viewmodel.JobDetailUiState
import com.stopgalere.presentation.viewmodel.JobDetailViewModel
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.solid.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.stopgalere.presentation.theme.StopGalereTheme
import compose.icons.fontawesomeicons.Solid
import com.RolandAssoh.stopgalere.ci.R
import androidx.core.net.toUri

@Composable
fun JobDetailScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: JobDetailViewModel = hiltViewModel()
) {
    BackHandler { navController.popBackStack() }

    // Single lifecycle-aware collection (StateHolder)
    val state by viewModel.screenState.collectAsStateWithLifecycle()

    when (val ui = state.ui) {
        is JobDetailUiState.Loading -> Box(
            Modifier.fillMaxSize().semantics { testTag = "JobDetail_Loading" },
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }

        is JobDetailUiState.Error -> Box(
            Modifier.fillMaxSize().semantics { testTag = "JobDetail_Error" },
            contentAlignment = Alignment.Center
        ) { Text(ui.message ?: stringResource(R.string.screen_job_detail_not_loaded), color = MaterialTheme.colorScheme.error) }

        is JobDetailUiState.Success -> JobDetailContent(
            modifier = modifier,
            job = ui.job,
            isOnline = state.isOnline,
            onBack = { navController.popBackStack() }
        )
    }
}


@Composable
fun JobDetailContent(
    modifier: Modifier = Modifier,
    job: JobUi,
    isOnline: Boolean,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current

    // Save scroll across rotations (configuration changes)
    val scrollState = rememberSaveable(saver = androidx.compose.foundation.ScrollState.Saver) {
        androidx.compose.foundation.ScrollState(initial = 0)
    }

    val doWeb = remember(job.authorWebsite) {
        { url: String ->
            if (url.isNotBlank()) {
                val safe = if (url.startsWith("http")) url else "https://$url"
                ctx.startActivity(Intent(Intent.ACTION_VIEW, safe.toUri()))
            }
        }
    }
    val doCall1 = remember(job.authorMobile1) {
        { phone: String ->
            if (phone.isNotBlank()) ctx.startActivity(Intent(Intent.ACTION_DIAL, "tel:$phone".toUri()))
        }
    }
    val doCall2 = remember(job.authorMobile2) {
        { phone: String ->
            if (phone.isNotBlank()) ctx.startActivity(Intent(Intent.ACTION_DIAL, "tel:$phone".toUri()))
        }
    }
    val doMail = remember(job.authorEmail) {
        { email: String ->
            if (email.isNotBlank()) ctx.startActivity(Intent(Intent.ACTION_SENDTO, "mailto:$email".toUri()))
        }
    }
    val doShare = remember(job) {
        {
            val txt = buildString {
                appendLine(job.title)
                appendLine(job.company)
                appendLine(job.city)
                appendLine(job.description)
            }
            ctx.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"; putExtra(Intent.EXTRA_TEXT, txt)
            }, "Share"))
        }
    }



    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .semantics {
                testTag = "JobDetail"
                contentDescription = "Job detail screen for ${job.title}"
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar
        Row(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .semantics { testTag = "JobDetail_TopBar" },
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderIconButton(onClick = onBack, tag = "JobDetail_Back", imageVector = FontAwesomeIcons.Solid.ArrowLeft)
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .semantics { testTag = "JobDetail_Actions" },
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(job.authorMobile1.isNotEmpty() ){
                    HeaderIconButton(onClick = { doCall1(job.authorMobile1) }, tag = "JobDetail_Call1", imageVector = FontAwesomeIcons.Solid.PhoneAlt)
                }

                if(job.authorMobile2.isNotEmpty()){
                    HeaderIconButton(onClick = { doCall2(job.authorMobile2) }, tag = "JobDetail_Call2", imageVector = FontAwesomeIcons.Solid.Phone)
                }

                if (job.authorEmail.isNotEmpty()){
                    HeaderIconButton(onClick = { doMail(job.authorEmail) }, tag = "JobDetail_SendEmail", imageVector = FontAwesomeIcons.Solid.PaperPlane)
                }

                if (job.authorWebsite.isNotEmpty()) {
                    HeaderIconButton(onClick = { doWeb(job.authorWebsite) }, tag = "JobDetail_Website", imageVector = FontAwesomeIcons.Solid.Globe)
                }
                HeaderIconButton(onClick = { doShare() }, tag = "JobDetail_Share", imageVector = FontAwesomeIcons.Solid.Share)
            }
        }

        // Body
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(MaterialTheme.colorScheme.background)
                .semantics { testTag = "JobDetail_Body" },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                job.title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 8.dp, start = 12.dp, end = 12.dp)
                    .semantics { testTag = "JobDetail_Title" }
            )

            HeaderItem(stringResource(R.string.screen_job_detail_description))
            BodyItem(job.description, tag = "JobDetail_Description")

            HeaderItem(stringResource(R.string.screen_job_detail_company))
            BodyItem(job.company, tag = "JobDetail_Company")
            CompanyLogo(url = job.companyLogoUrl, isOnline = isOnline)

            HeaderItem(stringResource(R.string.screen_job_detail_publish))
            BodyItem(job.date, tag = "JobDetail_Date")

            HeaderItem(stringResource(R.string.screen_job_detail_deadline))
            Text(
                job.deadline,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.Red,
                modifier = Modifier.semantics { testTag = "JobDetail_Deadline" }
            )

            if (job.authorMobile1.isNotEmpty() || job.authorMobile2.isNotEmpty()) {
                HeaderItem(stringResource(R.string.screen_job_detail_contacts))
                val phone = job.authorMobile1 + "\n" + job.authorMobile2
                Text(
                    phone,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.semantics { testTag = "JobDetail_Contacts" }
                )
            }

            HeaderItem(stringResource(R.string.screen_job_detail_sector))
            BodyItem(job.sectorName, tag = "JobDetail_Sector")

            if (job.authorWebsite.isNotEmpty()) {
                HeaderItem(stringResource(R.string.screen_job_detail_website))
                BodyItem(job.authorWebsite, tag = "JobDetail_Web")
            }

            if (job.authorEmail.isNotEmpty()) {
                HeaderItem(stringResource(R.string.screen_job_detail_email))
                BodyItem(job.authorEmail, tag = "JobDetail_Email")
            }

            // Salary strip
            DetailSectionCard(
                backgroudColor = Orange,
                modifier = Modifier
                    .height(200.dp)
                    .semantics { testTag = "JobDetail_SalaryCard" }
            ) {
                Row(
                    Modifier.fillMaxSize().padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        job.salary,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White,
                        modifier = Modifier.semantics { testTag = "JobDetail_SalaryValue" }
                    )
                    Spacer(Modifier.width(12.dp))
                    Icon(
                        FontAwesomeIcons.Solid.PiggyBank,
                        contentDescription = null,
                        modifier = Modifier.size(59.dp),
                        tint = Color.White
                    )
                }
            }

            // Work details (contract / mode / city)
            DetailSectionCard(
                backgroudColor = Color.White,
                modifier = Modifier
                    .height(200.dp)
                    .semantics { testTag = "JobDetail_WorkDetailsCard" }
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(FontAwesomeIcons.Solid.Briefcase, null, modifier = Modifier.size(49.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        DetailChip(label = job.contractTypeName, isOnline = isOnline, isCity = false)
                        DetailChip(label = job.workModeName,   isOnline = isOnline, isCity = false)
                        DetailChip(label = job.city,           isOnline = isOnline, isCity = true)
                    }
                }
            }

            // Requirements strip
            DetailSectionCard(
                backgroudColor = Green,
                modifier = Modifier
                    .height(200.dp)
                    .semantics { testTag = "JobDetail_RequirementsCard" }
            ) {
                Column(
                    Modifier.fillMaxSize().padding(top = 8.dp, bottom = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        FontAwesomeIcons.Solid.Search,
                        contentDescription = null,
                        modifier = Modifier.size(49.dp),
                        tint = Color.White
                    )
                    DetailItem(job.genderName)
                    DetailItem(job.educationLevel)
                    DetailItem(job.experience)
                }
            }
            Spacer(Modifier.height(50.dp))
        }
    }
}

@Composable
fun CompanyLogo(modifier: Modifier = Modifier, url: String, isOnline: Boolean) {
    if (url.isNotEmpty() && url.contains("http") && isOnline) {
        AsyncImage(
            model = url,
            contentDescription = "Company Logo",
            modifier = modifier.size(80.dp).semantics { testTag = "JobDetail_CompanyLogo" }
        )
    }
}

@Composable
private fun HeaderIconButton(onClick: () -> Unit,
                       tag:String,
                       imageVector: ImageVector){
    IconButton(
        onClick = { onClick() },
        modifier = Modifier.semantics { testTag = tag },
        content = {
            Icon(imageVector,
                null,
                tint = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.size(20.dp)) }
    )
}
@Composable
private fun HeaderItem(text: String) {
    Spacer(Modifier.height(12.dp))
    Text(
        text,
        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.semantics { testTag = "JobDetail_Header_$text" }
    )
}

@Composable
private fun BodyItem(text: String, tag: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.semantics {
            testTag = tag
            contentDescription = text
        }
    )
    Spacer(Modifier.height(12.dp))
}

@Composable
private fun DetailItem(text: String) {
    Spacer(Modifier.height(4.dp))
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
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
@Composable fun Preview_JobDetail_Small_Light()  { StopGalereTheme (dynamicColor = true) { JobDetailContent(job = JobDetailPreviewData.job, isOnline = false, onBack = {}) } }
@Preview(name = "Medium – Light", widthDp = 360, heightDp = 740, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable fun Preview_JobDetail_Medium_Light() { StopGalereTheme(dynamicColor = false) { JobDetailContent(job = JobDetailPreviewData.job, isOnline = false, onBack = {}) } }
@Preview(name = "Large – Light",  widthDp = 411, heightDp = 891, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable fun Preview_JobDetail_Large_Light()  { StopGalereTheme { JobDetailContent(job = JobDetailPreviewData.job, isOnline = false, onBack = {}) } }
@Preview(name = "Tablet – Light", widthDp = 800, heightDp = 1280, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable fun Preview_JobDetail_Tablet_Light() { StopGalereTheme { JobDetailContent(job = JobDetailPreviewData.job, isOnline = false, onBack = {}) } }
@Preview(name = "Medium – Dark",  widthDp = 360, heightDp = 740, showBackground = true, backgroundColor = 0xFF000000)
@Composable fun Preview_JobDetail_Medium_Dark()  { StopGalereTheme { JobDetailContent(job = JobDetailPreviewData.job, isOnline = false, onBack = {}) } }
