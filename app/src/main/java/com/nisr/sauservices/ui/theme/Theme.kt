package com.nisr.sauservices.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = PrimaryBlue,
        onPrimary = White,
        primaryContainer = PrimaryLight,
        onPrimaryContainer = PrimaryDark,
        secondary = PrimaryBlue,
        onSecondary = White,
        background = AppBackground,
        onBackground = Black,
        surface = White,
        onSurface = Black,
        surfaceVariant = LightGray,
        onSurfaceVariant = GrayText,
        outline = GrayDivider
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
