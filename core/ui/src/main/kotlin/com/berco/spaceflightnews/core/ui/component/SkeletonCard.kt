package com.berco.spaceflightnews.core.ui.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.berco.spaceflightnews.core.ui.preview.PreviewSurface
import com.berco.spaceflightnews.core.ui.theme.OrganicRadius

private const val PULSE_MILLIS = 1400

@Composable
fun SkeletonCard(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(PULSE_MILLIS),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )

    Surface(
        modifier = modifier.fillMaxWidth().alpha(alpha),
        shape = RoundedCornerShape(OrganicRadius.Card),
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(Modifier.padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 8.dp)) {
            Bar(height = 184.dp, widthFraction = 1f, shape = RoundedCornerShape(OrganicRadius.CardImage))
            Column(
                Modifier.padding(start = 4.dp, top = 14.dp, end = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Bar(height = 20.dp, widthFraction = 0.95f)
                Bar(height = 20.dp, widthFraction = 0.7f)
                Bar(height = 12.dp, widthFraction = 1f)
                Bar(height = 12.dp, widthFraction = 0.55f)
            }
            Row(
                Modifier.padding(start = 4.dp, top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Bar(height = 10.dp, widthFraction = 0.3f, modifier = Modifier.weight(1f))
                Box(
                    Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.outlineVariant),
                )
            }
        }
    }
}

@Composable
private fun Bar(
    height: Dp,
    widthFraction: Float,
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = CircleShape,
) {
    Box(
        modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(shape)
            .background(MaterialTheme.colorScheme.outlineVariant),
    )
}

@Preview(name = "Skeleton", widthDp = 412, showBackground = true)
@Composable
private fun SkeletonCardPreview() {
    PreviewSurface { SkeletonCard() }
}
