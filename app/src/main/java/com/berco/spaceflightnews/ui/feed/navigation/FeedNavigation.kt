package com.berco.spaceflightnews.ui.feed.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.berco.spaceflightnews.ui.feed.FeedScreen
import kotlinx.serialization.Serializable

@Serializable
data object FeedRoute

fun NavGraphBuilder.feedScreen(
    onArticleClick: (Long) -> Unit,
) {
    composable<FeedRoute> {
        FeedScreen(onArticleClick = onArticleClick)
    }
}
