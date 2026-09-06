package com.berco.spaceflightnews.core.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/**
 * Lucide glyphs at stroke-width 2.75, as the Organic system specifies. Built
 * from the design's own SVG path data so the shapes match exactly rather than
 * approximating with Material's icon set.
 */
object OrganicIcons {

    private fun circle(cx: Float, cy: Float, r: Float) =
        "M${cx - r} ${cy}a$r $r 0 1 0 ${r * 2} 0a$r $r 0 1 0 ${-r * 2} 0"

    private const val HEART =
        "M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7Z"
    private const val SEARCH = "M11 3a8 8 0 1 0 0 16 8 8 0 0 0 0-16Zm10 18-4.3-4.3"
    private const val ARROW_LEFT = "M19 12H5M12 19l-7-7 7-7"
    private const val CLOSE = "M18 6 6 18M6 6l12 12"
    private const val CHEVRON_RIGHT = "m9 18 6-6-6-6"
    private const val ALERT =
        "M21.73 18 13.73 4a2 2 0 0 0-3.46 0l-8 14A2 2 0 0 0 4 21h16a2 2 0 0 0 1.73-3ZM12 9v4M12 17h.01"
    private const val REFRESH =
        "M21 12a9 9 0 0 0-9-9 9.75 9.75 0 0 0-6.74 2.74L3 8M3 3v5h5M3 12a9 9 0 0 0 9 9 9.75 9.75 0 0 0 6.74-2.74L21 16M21 21v-5h-5"
    private const val NEWSPAPER =
        "M15 18h-5M18 14h-8M4 22h16a2 2 0 0 0 2-2V4a2 2 0 0 0-2-2H8a2 2 0 0 0-2 2v16a2 2 0 0 1-2 2Zm0 0a2 2 0 0 1-2-2v-9c0-1.1.9-2 2-2h2M10 6h8v4h-8V6Z"
    private const val EXTERNAL_LINK =
        "M15 3h6v6M10 14 21 3M18 13v6a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h6"
    private val SHARE = "M8.6 13.5l6.8 4M15.4 6.5l-6.8 4" +
        circle(18f, 5f, 3f) + circle(6f, 12f, 3f) + circle(18f, 19f, 3f)

    val HeartFilled: ImageVector = filled(HEART)
    val HeartOutline: ImageVector = outlined(HEART)
    val Search: ImageVector = outlined(SEARCH)
    val ArrowLeft: ImageVector = outlined(ARROW_LEFT)
    val Close: ImageVector = outlined(CLOSE)
    val ChevronRight: ImageVector = outlined(CHEVRON_RIGHT)
    val Alert: ImageVector = outlined(ALERT)
    val Refresh: ImageVector = outlined(REFRESH)
    val Newspaper: ImageVector = outlined(NEWSPAPER)
    val ExternalLink: ImageVector = outlined(EXTERNAL_LINK)
    val Share: ImageVector = outlined(SHARE)

    private fun outlined(pathData: String): ImageVector = builder().apply {
        addPath(
            pathData = addPathNodes(pathData),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2.75f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
    }.build()

    private fun filled(pathData: String): ImageVector = builder().apply {
        addPath(
            pathData = addPathNodes(pathData),
            fill = SolidColor(Color.Black),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2.75f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
    }.build()

    private fun builder() = ImageVector.Builder(
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    )
}
