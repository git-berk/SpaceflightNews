package com.berco.spaceflightnews.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.berco.spaceflightnews.core.ui.preview.ComponentPreviews
import com.berco.spaceflightnews.core.ui.preview.PreviewSurface
import com.berco.spaceflightnews.core.ui.theme.OrganicRadius

private const val SATURATION = 0.6f
private const val CONTRAST = 0.85f
private const val BRIGHTNESS = 1.1f
private const val OPACITY = 0.94f

/**
 * The Organic system's `.washed` treatment — CSS
 * `saturate(.6) contrast(.85) brightness(1.1) opacity(.94)` — so photography
 * sits back into the warm ground instead of on top of it.
 */
private val washedFilter: ColorFilter = ColorFilter.colorMatrix(
    ColorMatrix().apply {
        setToSaturation(SATURATION)
        val scale = BRIGHTNESS * CONTRAST
        val translate = (1f - CONTRAST) * 0.5f * 255f
        val v = values
        for (row in 0..2) {
            for (col in 0..3) {
                v[row * 5 + col] = v[row * 5 + col] * scale
            }
            v[row * 5 + 4] = translate
        }
    },
)

@Composable
fun WashedImage(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    Box(modifier.background(MaterialTheme.colorScheme.surfaceContainerHighest)) {
        if (url != null) {
            AsyncImage(
                model = url,
                contentDescription = contentDescription,
                contentScale = ContentScale.Crop,
                colorFilter = washedFilter,
                modifier = Modifier.matchParentSize().alpha(OPACITY),
            )
        }
    }
}

@ComponentPreviews
@Composable
private fun WashedImagePreview() {
    PreviewSurface {
        WashedImage(
            url = null,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(184.dp)
                .clip(RoundedCornerShape(OrganicRadius.CardImage)),
        )
    }
}
