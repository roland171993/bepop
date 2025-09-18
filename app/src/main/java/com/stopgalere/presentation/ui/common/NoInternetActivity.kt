package com.stopgalere.presentation.ui.common

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.RolandAssoh.stopgalere.ci.R
import com.stopgalere.presentation.theme.StopGalereTheme
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Ban

class NoInternetActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StopGalereTheme {
                Surface { NoInternetScreen() }
            }
        }
    }
}

@Composable
private fun NoInternetScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // FontAwesome WiFi-Slash icon
        Icon(
            imageVector = FontAwesomeIcons.Solid.Ban,
            contentDescription = "No internet icon",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.no_internet),
            style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.error),
            textAlign = TextAlign.Center
        )
    }
}

// PREVIEWS

@androidx.compose.ui.tooling.preview.Preview(
    name = "Small – Light",
    widthDp = 320, heightDp = 640, showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable
private fun Preview_NoInternet_Small_Light() {
    StopGalereTheme {
        Surface { NoInternetScreen() }
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Medium – Light",
    widthDp = 360, heightDp = 740, showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable
private fun Preview_NoInternet_Medium_Light() {
    StopGalereTheme {
        Surface { NoInternetScreen() }
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Large – Light",
    widthDp = 411, heightDp = 891, showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable
private fun Preview_NoInternet_Large_Light() {
    StopGalereTheme {
        Surface { NoInternetScreen() }
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "Medium – Dark",
    widthDp = 360, heightDp = 740, showBackground = true, backgroundColor = 0xFF000000,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun Preview_NoInternet_Medium_Dark() {
    StopGalereTheme {
        Surface { NoInternetScreen() }
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    name = "DynamicColor = false",
    widthDp = 360, heightDp = 740, showBackground = true, backgroundColor = 0xFFFFFFFF
)
@Composable
private fun Preview_NoInternet_DynamicColor_Off() {
    StopGalereTheme(dynamicColor = false) {
        Surface { NoInternetScreen() }
    }
}
