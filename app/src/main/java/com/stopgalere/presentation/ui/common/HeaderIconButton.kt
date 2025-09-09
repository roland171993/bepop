package com.stopgalere.presentation.ui.common

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.dp

@Composable
fun HeaderIconButton(onClick: () -> Unit, tag: String, imageVector: ImageVector) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.semantics { testTag = tag }
    ) {
        Icon(
            imageVector,
            null,
            tint = MaterialTheme.colorScheme.onSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}