package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CelestialGold,
    onPrimary = OnGold,
    primaryContainer = GoldContainer,
    onPrimaryContainer = CelestialGoldLight,
    secondary = PropheticViolet,
    onSecondary = Color(0xFF1E103B),
    secondaryContainer = PropheticVioletContainer,
    onSecondaryContainer = PropheticVioletLight,
    tertiary = AfricanOchre,
    onTertiary = Color(0xFF3E120F),
    tertiaryContainer = AfricanOchreContainer,
    onTertiaryContainer = Color(0xFFFFDAD4),
    background = MidnightObsidian,
    onBackground = TextPrimary,
    surface = DeepNavy,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceCardBorder,
    error = CrimsonAlert
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to elegant dark theme for sacred spiritual experience
    dynamicColor: Boolean = false, // Keep consistent spiritual obsidian and gold branding
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
