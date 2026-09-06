package com.berco.spaceflightnews.core.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/**
 * Lucide glyphs at stroke-width 2.75, as specified by the Organic system.
 * Built from the SVG path data so the shapes match the design exactly rather
 * than approximating with Material's icon set.
 */
object OrganicIcons {

    private const val HEART =
        "M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7Z"
    private const val SEARCH = "M11 3a8 8 0 1 0 0 16 8 8 0 0 0 0-16Zm10 18-4.3-4.3"
    private const val ARROW_LEFT = "M19 12H5M12 19l-7-7 7-7"
    private const val SHARE = "M8.6 13.5l6.8 4M15.4 6.5l-6.8 4"
    private const val CLOSE = "M18 6 6 18M6 6l12 12"

    val HeartFilled: ImageVector = filled(HEART)
    val HeartOutline: ImageVector = outlined(HEART)
    val Search: ImageVector = outlined(SEARCH)
    val ArrowLeft: ImageVector = outlined(ARROW_LEFT)
    val Share: ImageVector = outlined(SHARE, extraCircles = true)
    val Close: ImageVector = outlined(CLOSE)

    private fun outlined(pathData: String, extraCircles: Boolean = false): ImageVector =
        builder().apply {
            addPath(
                pathData = addPathNodes(pathData),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
            if (extraCircles) {
                listOf(18f to 5f, 6f to 12f, 18f to 19f).forEach { (cx, cy) ->
                    addPath(
                        pathData = addPathNodes(circle(cx, cy, 3f)),
                        stroke = SolidColor(Color.Black),
                        strokeLineWidth = 2.75f,
                        strokeLineCap = StrokeCap.Round,
                        strokeLineJoin = StrokeJoin.Round,
                    )
                }
            }
        }.build()

    private fun filled(pathData: String): ImageVector =
        builder().apply {
            addPath(
                pathData = addPathNodes(pathData),
                fill = SolidColor(Color.Black),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.75f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            )
        }.build()

    private fun circle(cx: Float, cy: Float, r: Float) =
        "M${cx - r} ${cy}a$r $r 0 1 0 ${r * 2} 0a$r $r 0 1 0 ${-r * 2} 0"

    private fun builder() = ImageVector.Builder(
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    )
}
