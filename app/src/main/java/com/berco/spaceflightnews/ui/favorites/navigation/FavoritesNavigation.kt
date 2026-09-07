package com.berco.spaceflightnews.ui.favorites.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.berco.spaceflightnews.ui.favorites.FavoritesScreen
import kotlinx.serialization.Serializable

@Serializable
data object FavoritesRoute

fun NavGraphBuilder.favoritesScreen(
    onArticleClick: (Long) -> Unit,
    onBrowseFeed: () -> Unit,
) {
    composable<FavoritesRoute> {
        FavoritesScreen(
            onArticleClick = onArticleClick,
            onBrowseFeed = onBrowseFeed,
        )
    }
}
