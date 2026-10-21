package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val QuranifyDarkColorScheme = darkColorScheme(
    primary = AccentBlue,
    onPrimary = TextMain,
    secondary = AccentCyan,
    onSecondary = BgDarkFrame,
    tertiary = AccentLightBlue,
    background = BgDarkFrame,
    onBackground = TextMain,
    surface = CardDarkSurface,
    onSurface = TextMain,
    surfaceVariant = CardElevatedSurface,
    onSurfaceVariant = TextMuted
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = QuranifyDarkColorScheme,
        typography = Typography,
        content = content
    )
}
