package com.stopgalere.presentation.ui.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
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
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(width)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4FAAF5),
                        Color(0xFF2942E1)
                    )
                )
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
                    tint          = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text  = item.label,
                    color = Color.White
                )
            }
            if (index < DrawerNavItemDefaults.items.lastIndex) {
                Divider(color = Color.White, thickness = 1.dp)
            }
        }
    }
}
