package com.stopgalere.presentation.ui.about

import android.content.res.Configuration
import android.view.ViewGroup
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.RolandAssoh.stopgalere.ci.R
import com.stopgalere.presentation.theme.AppBackground
import com.stopgalere.presentation.theme.StopGalereTheme
import com.stopgalere.presentation.ui.common.DetailTopBar
import com.stopgalere.presentation.viewmodel.about.AboutViewModel
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Ban

/* ------------------------------------------- */
/*  Route composable: keeps nav/Hilt OUT of UI */
/*  so previews don’t crash.                   */
/* ------------------------------------------- */
@Composable
fun AboutScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    vm: AboutViewModel = hiltViewModel()
) {
    BackHandler { navController.popBackStack() }
    val isOnline = vm.isOnline.collectAsStateWithLifecycle().value


    AboutScreenContent(
        modifier = modifier,
        isOnline = isOnline,
        onBack = { navController.popBackStack() }
    )
}

/* ------------------------------------------------- */
/*  UI-only composable that previews can call safely */
/* ------------------------------------------------- */
@VisibleForTesting
@Composable
internal fun AboutScreenContent(
    modifier: Modifier = Modifier,
    isOnline: Boolean,
    onBack: () -> Unit = {}
) {
    // Single source of truth for dimensions to avoid “two size edits”
    val dimens = AboutDefaults.dimens

    // Save scroll pos across recompositions & previews
    val scrollState = rememberSaveable(saver = androidx.compose.foundation.ScrollState.Saver) {
        androidx.compose.foundation.ScrollState(initial = 0)
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = AppBackground)
            .systemBarsPadding()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar
        DetailTopBar(
            modifier = modifier,
            titleRes = R.string.screen_about_title,
            onNavClick = onBack,
            testTag = "About_TopBar",
            navTestTag = "About_Back"
        )


        Box(modifier = Modifier.systemBarsPadding()){
            // App Logo
            Image(
                painter = painterResource(id = R.drawable.ic_logo_rounded),
                contentDescription = stringResource(id = R.string.app_name),
                modifier = Modifier.padding(top = dimens.logoTop)
            )
        }


        // App Name
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = colorResource(id = R.color.black),
            modifier = Modifier.padding(top = dimens.spacingSmall)
        )

        // Version
        Text(
            text = stringResource(id = R.string.app_version),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = colorResource(id = R.color.black),
            modifier = Modifier.padding(top = dimens.spacingSmall),
            textAlign = TextAlign.Center
        )

        // Developer
        Text(
            text = stringResource(id = R.string.app_developper),
            textAlign = TextAlign.Center
        )

        // Slogan
        Text(
            text = stringResource(id = R.string.app_slogan),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = dimens.spacingSmall)
        )

        // CGU Title
        Text(
            text = stringResource(id = R.string.app_cgu),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = dimens.spacingSmall)
        )

        if(isOnline){
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        loadUrl("https://cgu.stopgalere.rolandassoh.com")
                    }
                },
                modifier = Modifier
                    .padding(top = dimens.spacingSmall)
                    .fillMaxWidth()
                    .weight(1f)
            )
        }else{
            // Offline placeholder (same footprint as WebView)
            Column(
                modifier = Modifier.padding(top = dimens.spacingSmall),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.Ban,
                    contentDescription = "No internet",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = stringResource(id = R.string.no_internet),
                    style = MaterialTheme.typography.titleMedium
                )

            }
        }

    }
}

/* -------------------------------------------------------------------- */
/*  Dimensions in one place so both runtime & previews share same sizes */
/*  → change once, reflected everywhere.                                */
/* -------------------------------------------------------------------- */
@Stable
data class AboutDimens(
    val screenPadding: Dp = 16.dp,
    val logoTop: Dp = 10.dp,
    val spacingSmall: Dp = 5.dp,
    val webViewHeight: Dp = 100.dp
)

@Immutable
object AboutDefaults {
    val dimens = AboutDimens()
}

/* ----------------------------- */
/*  Lightweight preview theme    */
/*  NOTE: Replace with your app  */
/*  theme if you have one, e.g.  */
/*  StopGalereTheme(darkTheme,   */
/*  dynamicColor).               */
/* ----------------------------- */
@Composable
private fun PreviewTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean, // kept for parity; no-op here unless wired to your theme
    content: @Composable () -> Unit
) {
    // If you already have StopGalereTheme(darkTheme, dynamicColor),
    // call that here instead of plain MaterialTheme.
    StopGalereTheme {
        content()
    }
}

/* ===================== */
/* ===== PREVIEWS ====== */
/* ===================== */

/* Small – Light */
@androidx.compose.ui.tooling.preview.Preview(
    name = "Small – Light",
    widthDp = 320, heightDp = 640,
    showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable
private fun Preview_About_Small_Light() {
    PreviewTheme(darkTheme = false, dynamicColor = true) {
        AboutScreenContent(isOnline = false)
    }
}

/* Medium – Light */
@androidx.compose.ui.tooling.preview.Preview(
    name = "Medium – Light",
    widthDp = 360, heightDp = 740,
    showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable
private fun Preview_About_Medium_Light() {
    PreviewTheme(darkTheme = false, dynamicColor = true) {
        AboutScreenContent(isOnline = false)
    }
}

/* Large – Light */
@androidx.compose.ui.tooling.preview.Preview(
    name = "Large – Light",
    widthDp = 411, heightDp = 891,
    showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable
private fun Preview_About_Large_Light() {
    PreviewTheme(darkTheme = false, dynamicColor = true) {
        AboutScreenContent(isOnline = false)
    }
}

/* Medium – Dark */
@androidx.compose.ui.tooling.preview.Preview(
    name = "Medium – Dark",
    widthDp = 360, heightDp = 740,
    showBackground = true, backgroundColor = 0xFF000000,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun Preview_About_Medium_Dark() {
    PreviewTheme(darkTheme = true, dynamicColor = true) {
        AboutScreenContent(isOnline = false)
    }
}

/* DynamicColor = false (old devices) */
@androidx.compose.ui.tooling.preview.Preview(
    name = "DynamicColor = false",
    widthDp = 360, heightDp = 740,
    showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable
private fun Preview_About_Medium_Light_NoDynamic() {
    // If you have a real theme function that supports dynamic color flags,
    // replace PreviewTheme(...) with StopGalereTheme(darkTheme = false, dynamicColor = false)
    PreviewTheme(darkTheme = false, dynamicColor = false) {
        AboutScreenContent(isOnline = false)
    }
}
