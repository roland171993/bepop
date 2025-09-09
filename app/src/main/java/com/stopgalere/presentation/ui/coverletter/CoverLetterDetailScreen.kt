package com.stopgalere.presentation.ui.coverletter

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.stopgalere.presentation.viewmodel.coverletter.CoverLetterDetailUiState
import com.stopgalere.presentation.viewmodel.coverletter.CoverLetterDetailViewModel
import com.stopgalere.presentation.theme.StopGalereTheme
import com.RolandAssoh.stopgalere.ci.R
import com.stopgalere.presentation.preview.FakeData
import com.stopgalere.presentation.preview.FakeData.coverLetter
import com.stopgalere.presentation.ui.common.HeaderIconButton
import com.stopgalere.presentation.ui.coverletter.components.CoverLetterUi
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ArrowLeft
import compose.icons.fontawesomeicons.solid.FileAlt
import compose.icons.fontawesomeicons.solid.User

/**
 * Screen for displaying a single Cover Letter details
 */
@Composable
fun CoverLetterDetailScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: CoverLetterDetailViewModel = hiltViewModel()
) {
    BackHandler { navController.popBackStack() }

    val state by viewModel.screenState.collectAsStateWithLifecycle()

    when (val ui = state.ui) {
        is CoverLetterDetailUiState.Loading -> Box(
            Modifier.fillMaxSize().semantics { testTag = "CoverDetail_Loading" },
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }

        is CoverLetterDetailUiState.Error -> Box(
            Modifier.fillMaxSize().semantics { testTag = "CoverDetail_Error" },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = ui.message.asString(),
                color = MaterialTheme.colorScheme.error
            )
        }

        is CoverLetterDetailUiState.Success -> CoverLetterDetailContent(
            modifier = modifier,
            cover = ui.coverLetter,
            onBack = { navController.popBackStack() }
        )
    }
}

/**
 * UI Content for Cover Letter detail
 */
@Composable
fun CoverLetterDetailContent(
    modifier: Modifier = Modifier,
    cover: CoverLetterUi,
    onBack: () -> Unit
) {
    val scrollState = rememberSaveable(saver = androidx.compose.foundation.ScrollState.Saver) {
        androidx.compose.foundation.ScrollState(initial = 0)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .semantics {
                testTag = "CoverDetail"
                contentDescription = "Cover letter detail for ${cover.title}"
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar
        Row(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .semantics { testTag = "CoverDetail_TopBar" },
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderIconButton(
                onClick = onBack,
                tag = "CoverDetail_Back",
                imageVector = FontAwesomeIcons.Solid.ArrowLeft
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = stringResource(R.string.screen_cover_letters_detail_title),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        // Body
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(MaterialTheme.colorScheme.background)
                .semantics { testTag = "CoverDetail_Body" },
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                cover.title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .padding(12.dp)
                    .semantics { testTag = "CoverDetail_Title" },
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                cover.content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.semantics {
                    testTag = "CoverDetail_Content"
                    contentDescription = "Cover letter detail for ${cover.content}"
                }
            )

            Spacer(Modifier.height(50.dp))  // fix cut on real phone cause scafold not used , we want from stratch
        }
    }
}



/**
 * Previews for different devices and themes
 */
@Preview(name = "Small – Light", widthDp = 320, heightDp = 640, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable fun Preview_CoverDetail_Small_Light()  {
    StopGalereTheme(dynamicColor = true) {
        CoverLetterDetailContent(cover = coverLetter, onBack = {})
    }
}

@Preview(name = "Medium – Light", widthDp = 360, heightDp = 740, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable fun Preview_CoverDetail_Medium_Light() {
    StopGalereTheme(dynamicColor = false) {
        CoverLetterDetailContent(cover = coverLetter, onBack = {})
    }
}

@Preview(name = "Large – Light", widthDp = 411, heightDp = 891, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable fun Preview_CoverDetail_Large_Light()  {
    StopGalereTheme {
        CoverLetterDetailContent(cover = coverLetter, onBack = {})
    }
}

@Preview(name = "Tablet – Light", widthDp = 800, heightDp = 1280, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable fun Preview_CoverDetail_Tablet_Light() {
    StopGalereTheme { CoverLetterDetailContent(cover = coverLetter, onBack = {})
    }
}

@Preview(name = "Medium – Dark", widthDp = 360, heightDp = 740, showBackground = true, backgroundColor = 0xFF000000, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable fun Preview_CoverDetail_Medium_Dark()  {
    StopGalereTheme {
        CoverLetterDetailContent(cover = coverLetter, onBack = {})
    }
}
