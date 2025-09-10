package com.stopgalere.presentation.ui.coverletter.components

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stopgalere.presentation.preview.FakeData
import com.stopgalere.presentation.theme.StopGalereTheme

@Composable
fun CoverLetterItem(
    cover: CoverLetterUi,
    modifier: Modifier = Modifier,
    onClick: (CoverLetterUi) -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clickable { onClick(cover) }
            .semantics { testTag = "CoverLetterCard_${cover.title}" }
    ) {
        Box(modifier = modifier
            .fillMaxSize()
            .padding(vertical = 16.dp, horizontal = 12.dp), contentAlignment = Alignment.Center){
            Text(
                text = cover.title.uppercase(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

    }
}

// ------------------------------- Preview setup -------------------------------

@Composable
private fun CoverLetterItemPreviewContent(dynamicColor: Boolean = true) {
    StopGalereTheme(dynamicColor = dynamicColor) {
        Surface {
            CoverLetterItem(
                cover = FakeData.coverLetter
            )
        }
    }
}

//------------------------------- Previews -------------------------------

@Preview(
    name = "Phone – light",
    widthDp = 360, heightDp = 740,
    showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable
fun Preview_Phone_Light() {
    CoverLetterItemPreviewContent(dynamicColor = true)
}

@Preview(
    name = "Phone – dark",
    widthDp = 360, heightDp = 740,
    showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun Preview_Phone_Dark() {
    CoverLetterItemPreviewContent(dynamicColor = true)
}

@Preview(
    name = "Medium – light",
    widthDp = 411, heightDp = 891,
    showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable
fun Preview_Medium_Light() {
    CoverLetterItemPreviewContent(dynamicColor = true)
}

@Preview(
    name = "Tablet – light",
    widthDp = 800, heightDp = 1280,
    showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable
fun Preview_Tablet_Light() {
    CoverLetterItemPreviewContent(dynamicColor = true)
}

@Preview(
    name = "Old device (dynamicColor = false)",
    widthDp = 360, heightDp = 740,
    showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable
fun Preview_OldDevice_NoDynamic() {
    CoverLetterItemPreviewContent(dynamicColor = false)
}


