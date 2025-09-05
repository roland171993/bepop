package com.stopgalere.presentation.ui.coverletter

import android.content.res.Configuration
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.stopgalere.presentation.theme.StopGalereTheme
import com.stopgalere.presentation.ui.coverletter.components.CoverLetterContent
import com.stopgalere.presentation.ui.coverletter.components.CoverLetterUi
import kotlinx.coroutines.flow.flowOf

/**
 * CoverLetter screen previews
 * - Small / Medium / Large (light)
 * - Medium (dark)
 * - Medium (dynamicColor = false) for old devices
 *
 * Uses fake PagingData to keep sizing identical across previews.
 */
@Preview(
    name = "Small – Light",
    widthDp = 320, heightDp = 640, showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable
private fun Preview_CoverLetter_Small_Light() {
    PreviewCoverLetterScreen()
}

@Preview(
    name = "Medium – Light",
    widthDp = 360, heightDp = 740, showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable
private fun Preview_CoverLetter_Medium_Light() {
    PreviewCoverLetterScreen()
}

@Preview(
    name = "Large – Light",
    widthDp = 411, heightDp = 891, showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable
private fun Preview_CoverLetter_Large_Light() {
    PreviewCoverLetterScreen()
}

@Preview(
    name = "Medium – Dark",
    widthDp = 360, heightDp = 740, showBackground = true, backgroundColor = 0xFF000000,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun Preview_CoverLetter_Medium_Dark() {
    PreviewCoverLetterScreen()
}

@Preview(
    name = "DynamicColor = false",
    widthDp = 360, heightDp = 740, showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable
private fun Preview_CoverLetter_NoDynamic() {
    PreviewCoverLetterScreen(dynamicColor = false)
}

// Internal preview host with fake data

@Composable
private fun PreviewCoverLetterScreen(dynamicColor: Boolean = true) {
    StopGalereTheme(dynamicColor = dynamicColor) {
        // Stable fake list so all previews share identical element sizes
        val fakePaging = flowOf(
            PagingData.from(
                listOf(
                    CoverLetterUi("1", "ANIMATEUR", "—", "01-09-2025", "2025-09-01"),
                    CoverLetterUi("2", "ARCHEOLOGUE", "—", "02-09-2025", "2025-09-02"),
                    CoverLetterUi("3", "ARCHITECTE D'INTERIEUR", "—", "03-09-2025", "2025-09-03"),
                    CoverLetterUi("4", "ARTISTE 3D", "—", "04-09-2025", "2025-09-04"),
                    CoverLetterUi("5", "ASSISTANT RH", "—", "05-09-2025", "2025-09-05")
                )
            )
        ).collectAsLazyPagingItems()

        CoverLetterContent(
            isSearchOpen = false,
            query = "",
            isOnline = true,
            onQueryChange = {},
            onSetSearchActive = {},
            onBack = {},
            covers = fakePaging,
            listState = rememberLazyListState(),
            onRefresh = {},
            onItemClick = {}
        )
    }
}
