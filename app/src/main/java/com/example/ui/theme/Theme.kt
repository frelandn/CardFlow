package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkFintechColorScheme = darkColorScheme(
    primary = NeonMint,
    onPrimary = Color(0xFF021B14),
    primaryContainer = NeonMintDark,
    onPrimaryContainer = Color(0xFFA7F3D0),
    secondary = ElectricCyan,
    onSecondary = Color(0xFF001F29),
    secondaryContainer = Color(0xFF083344),
    onSecondaryContainer = Color(0xFFBAE6FD),
    tertiary = VibrantAmber,
    onTertiary = Color(0xFF2E1500),
    tertiaryContainer = AmberDark,
    onTertiaryContainer = Color(0xFFFDE68A),
    background = FintechBackground,
    onBackground = TextPrimary,
    surface = FintechSurface,
    onSurface = TextPrimary,
    surfaceVariant = FintechSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    surfaceContainer = FintechSurfaceVariant,
    surfaceContainerHigh = FintechSurfaceHigh,
    error = NeonRose,
    onError = Color.White
)

@Composable
fun CardFlowTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkFintechColorScheme,
        typography = Typography,
        content = content
    )
}
