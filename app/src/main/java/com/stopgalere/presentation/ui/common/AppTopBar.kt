package com.stopgalere.presentation.ui.common

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ArrowLeft

@Composable
fun DetailTopBar(
    title: String,
    onNavClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = FontAwesomeIcons.Solid.ArrowLeft,
    testTag: String = "CoverDetail_TopBar",
    navTestTag: String = "CoverDetail_Back",
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
) {
    Row(
        modifier
            .fillMaxWidth()
            .background(containerColor)
            .semantics { this.testTag = testTag },
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Use your existing HeaderIconButton if available; otherwise fall back to IconButton
        HeaderIconButton(
            onClick = onNavClick,
            tag = navTestTag,
            imageVector = icon
        )

        Spacer(Modifier.width(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

    }
}

// Convenience overload when you have a string resource id
@Composable
fun DetailTopBar(
    @StringRes titleRes: Int,
    onNavClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = FontAwesomeIcons.Solid.ArrowLeft,
    testTag: String = "Detail_TopBar",
    navTestTag: String = "Detail_Back",
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary
) = DetailTopBar(
    title = stringResource(titleRes),
    onNavClick = onNavClick,
    modifier = modifier,
    icon = icon,
    testTag = testTag,
    navTestTag = navTestTag,
    containerColor = containerColor,
    contentColor = contentColor
)