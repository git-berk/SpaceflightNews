package com.berco.spaceflightnews.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val OrganicShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(22.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

object OrganicRadius {
    val Sm = 8.dp
    val Md = 16.dp
    val Lg = 28.dp
    val Card = 32.dp
    val CardImage = 22.dp
    val Row = 26.dp
    val Pill = 999.dp
}

object OrganicSpacing {
    val S1 = 4.dp
    val S2 = 9.dp
    val S3 = 13.dp
    val S4 = 18.dp
    val S6 = 26.dp
    val S8 = 35.dp
}
