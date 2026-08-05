package com.nisr.sauservices.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = OrchidPrimary,
        onPrimary = White,
        primaryContainer = OrchidLight,
        onPrimaryContainer = OrchidDark,
        secondary = OrchidAccent,
        onSecondary = Black,
        background = OrchidBackground,
        onBackground = Black,
        surface = White,
        onSurface = Black
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
