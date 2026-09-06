package com.berco.spaceflightnews.core.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(name = "Organic palette", widthDp = 412, showBackground = true)
@Composable
private fun PalettePreview() {
    SpaceflightTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Ramp(
                    "accent",
                    listOf(
                        OrganicColors.Accent100, OrganicColors.Accent200, OrganicColors.Accent300,
                        OrganicColors.Accent400, OrganicColors.Accent, OrganicColors.Accent600,
                        OrganicColors.Accent700, OrganicColors.Accent800, OrganicColors.Accent900,
                    ),
                )
                Ramp(
                    "accent-2",
                    listOf(
                        OrganicColors.Accent2_100, OrganicColors.Accent2_200, OrganicColors.Accent2_300,
                        OrganicColors.Accent2_400, OrganicColors.Accent2, OrganicColors.Accent2_600,
                        OrganicColors.Accent2_700, OrganicColors.Accent2_800, OrganicColors.Accent2_900,
                    ),
                )
                Ramp(
                    "neutral",
                    listOf(
                        OrganicColors.Neutral100, OrganicColors.Neutral200, OrganicColors.Neutral300,
                        OrganicColors.Neutral400, OrganicColors.Neutral500, OrganicColors.Neutral600,
                        OrganicColors.Neutral700, OrganicColors.Neutral800, OrganicColors.Neutral900,
                    ),
                )
                Ramp("ground", listOf(OrganicColors.Bg, OrganicColors.Surface, OrganicColors.Text))
            }
        }
    }
}

@Composable
private fun Ramp(label: String, colors: List<Color>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            colors.forEach { color ->
                Box(
                    Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(OrganicRadius.Sm))
                        .background(color),
                )
            }
        }
    }
}

@Preview(name = "Organic type", widthDp = 412, showBackground = true)
@Composable
private fun TypePreview() {
    SpaceflightTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text("Spaceflight News", style = MaterialTheme.typography.displaySmall)
                Text("Isar Aerospace attempts launch", style = MaterialTheme.typography.headlineLarge)
                Text("Blue Origin expands test sites", style = MaterialTheme.typography.titleLarge)
                Text(
                    "The European private space launch provider Isar Aerospace is set to attempt " +
                        "the second flight of its Spectrum rocket.",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    "Blue Origin will design and launch the Mars Telecommunications Orbiter.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    "NASASPACEFLIGHT",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}
