package com.stopgalere.presentation.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.GoogleFont.Provider
import com.stopgalere.R

private val robotoGoogleFont = GoogleFont("Roboto")
private val googleFontProvider = Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage   = "com.google.android.gms",
    certificates      = R.array.com_google_android_gms_fonts_certs
)

val Roboto = FontFamily(
    Font(googleFont = robotoGoogleFont, fontProvider = googleFontProvider, weight = FontWeight.Normal),
    Font(googleFont = robotoGoogleFont, fontProvider = googleFontProvider, weight = FontWeight.Medium),
    Font(googleFont = robotoGoogleFont, fontProvider = googleFontProvider, weight = FontWeight.Bold)
)
