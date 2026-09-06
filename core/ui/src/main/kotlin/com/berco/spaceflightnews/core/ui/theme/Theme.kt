package com.berco.spaceflightnews.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val OrganicColorScheme = lightColorScheme(
    primary = OrganicColors.Accent,
    onPrimary = OrganicColors.Bg,
    primaryContainer = OrganicColors.Accent100,
    onPrimaryContainer = OrganicColors.Accent800,

    secondary = OrganicColors.Accent2,
    onSecondary = OrganicColors.Bg,
    secondaryContainer = OrganicColors.Accent2_100,
    onSecondaryContainer = OrganicColors.Accent2_800,

    tertiary = OrganicColors.Accent2_600,
    onTertiary = OrganicColors.Bg,

    background = OrganicColors.Bg,
    onBackground = OrganicColors.Text,

    surface = OrganicColors.Bg,
    onSurface = OrganicColors.Text,
    surfaceVariant = OrganicColors.Neutral200,
    onSurfaceVariant = OrganicColors.Neutral700,

    surfaceContainerLowest = OrganicColors.Neutral100,
    surfaceContainerLow = OrganicColors.Neutral100,
    surfaceContainer = OrganicColors.Surface,
    surfaceContainerHigh = OrganicColors.Neutral200,
    surfaceContainerHighest = OrganicColors.Neutral300,

    outline = OrganicColors.Neutral400,
    outlineVariant = OrganicColors.Neutral300,

    // The design keeps failure states in the warm accent family rather than
    // introducing a red, so the error roles map onto the accent ramp.
    error = OrganicColors.Accent700,
    onError = OrganicColors.Bg,
    errorContainer = OrganicColors.Accent100,
    onErrorContainer = OrganicColors.Accent800,
)

@Composable
fun SpaceflightTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = OrganicColorScheme,
        typography = OrganicTypography,
        shapes = OrganicShapes,
        content = content,
    )
}
