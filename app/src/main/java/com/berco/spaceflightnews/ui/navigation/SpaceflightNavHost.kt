package com.berco.spaceflightnews.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.berco.spaceflightnews.ui.detail.navigation.articleDetailScreen
import com.berco.spaceflightnews.ui.detail.navigation.navigateToArticleDetail
import com.berco.spaceflightnews.ui.favorites.navigation.FAVORITES_ROUTE
import com.berco.spaceflightnews.ui.favorites.navigation.favoritesScreen
import com.berco.spaceflightnews.ui.feed.navigation.FEED_ROUTE
import com.berco.spaceflightnews.ui.feed.navigation.feedScreen

@Composable
fun SpaceflightNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = FEED_ROUTE,
        modifier = modifier,
    ) {
        feedScreen(
            onArticleClick = navController::navigateToArticleDetail,
        )
        favoritesScreen(
            onArticleClick = navController::navigateToArticleDetail,
            onBrowseFeed = { navController.navigateToTopLevel(FEED_ROUTE) },
        )
        articleDetailScreen(
            onBack = navController::popBackStack,
        )
    }
}
