package com.berco.spaceflightnews.ui.favorites.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.berco.spaceflightnews.ui.favorites.FavoritesScreen

const val FAVORITES_ROUTE = "favorites_route"

fun NavGraphBuilder.favoritesScreen(
    onArticleClick: (Long) -> Unit,
    onBrowseFeed: () -> Unit,
) {
    composable(route = FAVORITES_ROUTE) {
        FavoritesScreen(
            onArticleClick = onArticleClick,
            onBrowseFeed = onBrowseFeed,
        )
    }
}
