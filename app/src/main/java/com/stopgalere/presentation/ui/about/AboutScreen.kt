package com.stopgalere.presentation.ui.about

import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.RolandAssoh.stopgalere.ci.R

@Composable
fun AboutScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
){
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.app_background))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Logo
        Image(
            painter = painterResource(id = R.mipmap.ic_launcher),
            contentDescription = stringResource(id = R.string.app_name),
            modifier = Modifier
                .padding(top = 10.dp)
        )

        // App Name
        Text(
            text = stringResource(id = R.string.app_name),
            fontSize = dimensionResource(id = R.dimen.item_mobile_size).value.sp,
            color = colorResource(id = R.color.black),
            modifier = Modifier.padding(top = 5.dp)
        )

        // Version
        Text(
            text = stringResource(id = R.string.app_version),
            fontSize = dimensionResource(id = R.dimen.item_mobile_size).value.sp,
            color = colorResource(id = R.color.black),
            modifier = Modifier.padding(top = 5.dp)
        )

        // Developer
        Text(
            text = stringResource(id = R.string.app_developper),
            color = colorResource(id = R.color.black)
        )

        // Slogan
        Text(
            text = stringResource(id = R.string.app_slogan),
            color = colorResource(id = R.color.black),
            modifier = Modifier.padding(top = 5.dp)
        )

        // CGU Title
        Text(
            text = stringResource(id = R.string.app_cgu),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 5.dp)
        )

        // WebView (interop with AndroidView)
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
                .padding(top = 10.dp)
                .fillMaxWidth()
                .weight(1f) // take the rest of the screen
        )
    }
}