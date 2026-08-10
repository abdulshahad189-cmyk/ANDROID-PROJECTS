package com.nisr.sauservices.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SAUColorScheme = lightColorScheme(

    primary = SAUPrimary,

    onPrimary = SAUWhite,

    primaryContainer = SAULightBlue,

    onPrimaryContainer = SAUPrimaryDark,

    secondary = SAUPrimaryLight,

    onSecondary = SAUWhite,

    secondaryContainer = SAUBackgroundSoft,

    onSecondaryContainer = SAUPrimaryDark,

    background = SAUBackground,

    onBackground = SAUText,

    surface = SAUWhite,

    onSurface = SAUText,

    surfaceVariant = SAUBackgroundSoft,

    onSurfaceVariant = SAUTextSecondary,

    outline = SAUBorder,

    error = SAUError,

    onError = SAUWhite
)

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(

        colorScheme = SAUColorScheme,

        typography = Typography(),

        content = content
    )
}