@file:OptIn(ExperimentalFoundationApi::class)

package com.stopgalere.presentation.ui.intro

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.RolandAssoh.stopgalere.ci.R
import com.stopgalere.presentation.theme.StopGalereTheme
import kotlinx.coroutines.launch

@Composable
fun WizardPagerScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // 1. Pages data
    val pages = listOf(
        WizardPage(R.string.screen_wizard_resume,       imageRes = null),
        WizardPage(R.string.screen_wizard_cover_letter, imageRes = null),
        WizardPage(R.string.screen_wizard_gps,          imageRes = R.drawable.img_wizard_map)
    )

    // 2. Pager state
    val pagerState = rememberPagerState(
        pageCount   = { pages.size },
        initialPage = 0
    )
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .navigationBarsPadding()      // lift above system nav
            .fillMaxSize()
            .semantics { testTag = "WizardPager" }
    ) {
        // 3. Full-screen primary background + pager
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
        ) {
            HorizontalPager(
                state    = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                WizardPageItem(
                    page     = pages[page],
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .semantics { testTag = "WizardPage_$page" }
                )
            }
        }

        // 4. Heart indicator, just above the button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .semantics { testTag = "WizardIndicator" },
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pages.size) { idx ->
                val selected = idx == pagerState.currentPage
                Icon(
                    imageVector        = if (selected) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    tint               = if (selected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    modifier           = Modifier
                        .size(28.dp)
                        .padding(horizontal = 6.dp)
                )
            }
        }

        // 5. Pill-shaped Next/Finish button
        val isLast = pagerState.currentPage == pages.lastIndex
        Button(
            onClick = {
                if (!isLast) {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                } else {
                    navController.navigate("main") {
                        popUpTo("intro") { inclusive = true }
                    }
                }
            },
            shape    = CircleShape,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp)
                .semantics { testTag = "WizardNextButton" }
        ) {
            Text(
                text      = stringResource(if (isLast) R.string.screen_wizard_finish else R.string.screen_wizard_next),
                color     = Color.White,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))  // bottom padding
    }
}

@Composable
private fun WizardPageItem(
    page: WizardPage,
    modifier: Modifier = Modifier
) {
    Column(
        modifier            = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // white, centered, multi-line text
        Text(
            text      = stringResource(page.textRes),
            style     = MaterialTheme.typography.displaySmall.copy(color = Color.White),
            textAlign = TextAlign.Center,
            modifier  = Modifier.fillMaxWidth()
        )

        // optional image (GPS page)
        page.imageRes?.let { res ->
            Image(
                painter            = painterResource(res),
                contentDescription = null,
                modifier           = Modifier
                    .padding(top = 24.dp)
                    .size(width = 260.dp, height = 160.dp)
            )
        }
    }
}

private data class WizardPage(
    val textRes: Int,
    val imageRes: Int?
)

@Preview(name = "Wizard • Small Phone",  widthDp = 320, heightDp = 640)
@Preview(name = "Wizard • Medium Phone", widthDp = 360, heightDp = 800)
@Preview(name = "Wizard • Large Phone",  widthDp = 411, heightDp = 891)
@Preview(name = "Wizard • Tablet",        widthDp = 800, heightDp = 1280)
@Composable
private fun PreviewWizardPager() {
    val navController = rememberNavController()
    StopGalereTheme {
        WizardPagerScreen(navController)
    }
}
