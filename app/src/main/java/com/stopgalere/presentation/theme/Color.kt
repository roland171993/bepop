package com.stopgalere.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val AppPrimary      = Color(0xFF1976D2)
private val AppPrimaryDark  = Color(0xFF1565C0)
private val AppOnPrimary    = Color.White

val MaterialBlue500 = Color(0xFF478FCC)
val MaterialGrey200 = Color(0xFFEEEEEE)
val MaterialGrey500 = Color(0xFF9D9D9D)
val MaterialGrey600 = Color(0xFF757575)

val Orange = Color(0xFFDB7A3D)

val Green = Color(0xFF4CAF50)

val SplashColor = Color(0xFF3375BA)
private val BackgroundColor = Color(0xFFE7E7E7)
private val TextColor       = Color(0xFF525252)

val LightColors = lightColorScheme(
    primary      = AppPrimary,
    onPrimary    = AppOnPrimary,
    secondary    = MaterialBlue500,
    background   = BackgroundColor,
    onBackground = TextColor,
    surface      = Color.White,
    onSurface    = TextColor,
    error        = Color(0xFFD32F2F),
    onError      = Color.White
)

val DarkColors = darkColorScheme(
    primary      = AppPrimaryDark,
    onPrimary    = AppOnPrimary,
    secondary    = MaterialBlue500,
    background   = Color.Black,
    onBackground = Color.White,
    surface      = Color(0xFF121212),
    onSurface    = Color.White,
    error        = Color(0xFFEF5350),
    onError      = Color.Black
)
