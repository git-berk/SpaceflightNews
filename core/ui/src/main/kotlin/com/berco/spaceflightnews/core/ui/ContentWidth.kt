package com.berco.spaceflightnews.core.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Past this a summary runs well beyond a comfortable reading measure and the
 * card's image becomes a billboard — which is what a rotated phone showed.
 * Wider windows get margins rather than wider content.
 */
val MaxContentWidth = 600.dp

/**
 * Fills the window until it reaches [MaxContentWidth], then centres and stops
 * growing. Applied to a whole screen so its top bar shares the column with its
 * content instead of the two drifting to opposite edges.
 */
fun Modifier.readableWidth(): Modifier = this
    .fillMaxWidth()
    .wrapContentWidth(Alignment.CenterHorizontally)
    .widthIn(max = MaxContentWidth)
    .fillMaxWidth()
