package com.stopgalere.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Roboto,
        fontWeight  = FontWeight.Normal,
        fontSize    = 40.sp,
        color       = MaterialGrey500
    ),
    headlineLarge = TextStyle(
        fontFamily = Roboto,
        fontWeight  = FontWeight.Normal,
        fontSize    = 20.sp,
        color       = MaterialGrey600
    ),
    titleLarge = TextStyle(
        fontFamily = Roboto,
        fontWeight  = FontWeight.Medium,
        fontSize    = 18.sp,
        color       = MaterialGrey200
    ),
    bodyLarge = TextStyle(
        fontFamily = Roboto,
        fontWeight  = FontWeight.Medium,
        fontSize    = 14.sp,
        color       = MaterialGrey600
    ),
    labelSmall = TextStyle(
        fontFamily = Roboto,
        fontWeight  = FontWeight.Normal,
        fontSize    = 12.sp,
        color       = MaterialGrey200
    )
)
