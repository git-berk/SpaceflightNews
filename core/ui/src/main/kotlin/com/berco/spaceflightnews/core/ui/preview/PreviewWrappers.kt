package com.berco.spaceflightnews.core.ui.preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.berco.spaceflightnews.core.ui.theme.SpaceflightTheme

/**
 * Renders each component at the phone width the design targets and again at
 * 2× font scale, which is where fixed-height clamped text tends to break.
 */
@Preview(name = "412dp", widthDp = 412, showBackground = true)
@Preview(name = "Font 2x", widthDp = 412, fontScale = 2f, showBackground = true)
annotation class ComponentPreviews

@Composable
internal fun PreviewSurface(content: @Composable () -> Unit) {
    SpaceflightTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            Box(Modifier.padding(16.dp)) { content() }
        }
    }
}
