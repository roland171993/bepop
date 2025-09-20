package com.stopgalere.presentation.ui.main.components

import androidx.annotation.StringRes
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
import androidx.compose.ui.res.stringResource
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
import com.RolandAssoh.stopgalere.ci.R


/**
 * A single item in the drawer: route, icon, and label.
 */
data class DrawerNavItem(
    val route: String,
    val icon: ImageVector,
    @StringRes val labelRes: Int
)

object DrawerNavItemDefaults {
    val items = listOf(
        DrawerNavItem(
            route = "coverLetter",
            icon  = FontAwesomeIcons.Solid.FileAlt,
            labelRes = R.string.screen_main_drawer_cover_letter
        ),
        DrawerNavItem(
            route = "resume",
            icon  = FontAwesomeIcons.Solid.IdCard,
            labelRes = R.string.screen_main_drawer_cv
        ),
        DrawerNavItem(
            route = "rate",
            icon  = FontAwesomeIcons.Solid.Star,
            labelRes = R.string.screen_main_drawer_rate
        ),
        DrawerNavItem(
            route = "about",
            icon  = FontAwesomeIcons.Solid.InfoCircle,
            labelRes = R.string.screen_main_drawer_about
        ),
        DrawerNavItem(
            route = "help",
            icon  = FontAwesomeIcons.Solid.QuestionCircle,
            labelRes = R.string.screen_main_drawer_help
        ),
        DrawerNavItem(
            route = "upload",
            icon  = FontAwesomeIcons.Solid.Bullhorn,
            labelRes = R.string.screen_main_drawer_upload
        ),
        DrawerNavItem(
            route = "page",
            icon  = FontAwesomeIcons.Brands.Facebook,
            labelRes = R.string.screen_main_drawer_page
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
            scheme.onPrimaryContainer
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
            val label = stringResource(item.labelRes)
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
                    contentDescription = label,
                    modifier      = Modifier.size(56.dp),
                    tint          = content
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text  = label,
                    color = content
                )
            }
            if (index < DrawerNavItemDefaults.items.lastIndex) {
                Divider(color = content, thickness = 1.dp)
            }
        }
    }
}
