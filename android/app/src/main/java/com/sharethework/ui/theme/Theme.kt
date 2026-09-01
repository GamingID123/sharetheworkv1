package com.sharethework.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = White,
    onPrimary = BlackBg,
    secondary = GreyCard,
    onSecondary = LightText,
    background = BlackBg,
    onBackground = LightText,
    surface = DarkCard,
    onSurface = LightText,
    surfaceVariant = GreyCard,
    error = ErrorRed
)

@Composable
fun ShareTheWorkTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AppTypography,
        content = content
    )
}
