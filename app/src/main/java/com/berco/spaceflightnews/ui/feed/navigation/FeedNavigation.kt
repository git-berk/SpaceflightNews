package com.berco.spaceflightnews.ui.feed.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.berco.spaceflightnews.ui.feed.FeedScreen

const val FEED_ROUTE = "feed_route"

fun NavGraphBuilder.feedScreen(
    onArticleClick: (Long) -> Unit,
) {
    composable(route = FEED_ROUTE) {
        FeedScreen(onArticleClick = onArticleClick)
    }
}
