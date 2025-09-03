package com.stopgalere.presentation.ui.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Brands
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.brands.Facebook
import compose.icons.fontawesomeicons.solid.Bullhorn
import compose.icons.fontawesomeicons.solid.FileAlt
import compose.icons.fontawesomeicons.solid.IdCard
import compose.icons.fontawesomeicons.solid.InfoCircle
import compose.icons.fontawesomeicons.solid.QuestionCircle
import compose.icons.fontawesomeicons.solid.Star


/**
 * A single item in the drawer: route, icon, and label.
 */
data class DrawerNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

object DrawerNavItemDefaults {
    val items = listOf(
        DrawerNavItem(
            route = "cover_letter",
            icon  = FontAwesomeIcons.Solid.FileAlt,
            label = "L.Motivation"
        ),
        DrawerNavItem(
            route = "cv",
            icon  = FontAwesomeIcons.Solid.IdCard,
            label = "CV"
        ),
        DrawerNavItem(
            route = "noter",
            icon  = FontAwesomeIcons.Solid.Star,
            label = "Noter"
        ),
        DrawerNavItem(
            route = "apropos",
            icon  = FontAwesomeIcons.Solid.InfoCircle,
            label = "A Propos"
        ),
        DrawerNavItem(
            route = "aide",
            icon  = FontAwesomeIcons.Solid.QuestionCircle,
            label = "Aide"
        ),
        DrawerNavItem(
            route = "deposer",
            icon  = FontAwesomeIcons.Solid.Bullhorn,
            label = "Déposer"
        ),
        DrawerNavItem(
            route = "page",
            icon  = FontAwesomeIcons.Brands.Facebook,
            label = "Page"
        )
    )
}

@Composable
fun DrawerContent(
    width: Dp,
    onItemSelected: (String) -> Unit
) {
    val scheme = MaterialTheme.colorScheme

    // Use a dynamic gradient based on Material You colors
    val bgBrush = Brush.verticalGradient(
        colors = listOf(
            scheme.primary,
            scheme.primaryContainer
        )
    )
    // A content color that keeps good contrast on the gradient
    val content = scheme.onPrimary

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(width)
            .background(
                brush = bgBrush
            )
    ) {
        DrawerNavItemDefaults.items.forEachIndexed { index, item ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clickable { onItemSelected(item.route) }
                    .semantics { testTag = "DrawerItem_${item.route}" }
            ) {
                Icon(
                    imageVector   = item.icon,
                    contentDescription = item.label,
                    modifier      = Modifier.size(56.dp),
                    tint          = content
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text  = item.label,
                    color = content
                )
            }
            if (index < DrawerNavItemDefaults.items.lastIndex) {
                Divider(color = content, thickness = 1.dp)
            }
        }
    }
}
