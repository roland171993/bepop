package com.stopgalere.presentation.ui.common

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.RolandAssoh.stopgalere.ci.R
import com.stopgalere.presentation.theme.StopGalereTheme

class NoInternetActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StopGalereTheme {
                Surface { NoInternetContent() }
            }
        }
    }
}

@Composable
private fun NoInternetContent() {
    Text(
        text = stringResource(id = R.string.no_internet),
        style = MaterialTheme.typography.titleLarge
    )
}
