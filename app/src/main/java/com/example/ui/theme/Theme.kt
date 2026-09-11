package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val JMusicColorScheme = darkColorScheme(
    primary = JMusicBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF0E2A54),
    onPrimaryContainer = JMusicBlueLight,

    secondary = JMusicCyan,
    onSecondary = Color(0xFF00363A),
    secondaryContainer = Color(0xFF004D54),
    onSecondaryContainer = Color(0xFF80DEEA),

    tertiary = JMusicSky,
    onTertiary = Color(0xFF00354E),
    tertiaryContainer = Color(0xFF083D59),
    onTertiaryContainer = Color(0xFFBAE6FD),

    background = JMusicDarkBg,
    onBackground = JMusicTextPrimary,

    surface = JMusicSurface,
    onSurface = JMusicTextPrimary,
    surfaceVariant = JMusicSurfaceVariant,
    onSurfaceVariant = JMusicTextSecondary,

    outline = JMusicCardBorder,
    outlineVariant = Color(0xFF17243A),

    error = JMusicRose,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = JMusicColorScheme,
        typography = Typography,
        content = content
    )
}
