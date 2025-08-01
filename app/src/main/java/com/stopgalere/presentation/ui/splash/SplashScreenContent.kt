package com.stopgalere.presentation.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.RolandAssoh.stopgalere.ci.R
import com.stopgalere.presentation.theme.SplashColor
import com.stopgalere.presentation.theme.StopGalereTheme

/**
 * Stateless Splash screen UI.
 */
@Composable
fun SplashScreenContent(modifier: Modifier = Modifier) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val topPadding = screenHeight * 0.15f
    val spacerSmall = screenHeight * 0.02f
    val spacerLarge = screenHeight * 0.15f

    Surface(
        modifier = modifier.fillMaxSize(),
        color = SplashColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(topPadding))

            Text(
                text = stringResource(R.string.screen_splash_title),
                style = TextStyle(
                    fontSize = 48.sp,
                    color = Color.White,
                    lineHeight = 57.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(spacerSmall))

            Text(
                text = stringResource(R.string.screen_splash_header),
                style = MaterialTheme.typography.headlineLarge.copy(color = Color.LightGray),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(spacerLarge))

            Image(
                painter = painterResource(R.drawable.ic_splash),
                contentDescription = null,
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

/**
 * Four-device previews for small/medium/large phone & tablet.
 */
@Preview(
    name = "Small Phone",
    device = "spec:width=320dp,height=480dp,dpi=160",
    showBackground = true
)
@Preview(
    name = "Medium Phone",
    device = "spec:width=360dp,height=640dp,dpi=320",
    showBackground = true
)
@Preview(
    name = "Large Phone",
    device = "spec:width=411dp,height=731dp,dpi=480",
    showBackground = true
)
@Preview(
    name = "Tablet",
    device = "spec:width=600dp,height=1024dp,dpi=160",
    showBackground = true
)
@Composable
fun SplashScreenContentPreview() {
    StopGalereTheme {
        SplashScreenContent()
    }
}