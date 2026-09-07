package com.berco.spaceflightnews.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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

private const val TAB_FADE_MILLIS = 200

@Composable
fun SpaceflightNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = FEED_ROUTE,
        modifier = modifier,
        // Tabs are siblings, so they cross-fade. The detail destination
        // overrides this with a horizontal slide.
        enterTransition = { fadeIn(tween(TAB_FADE_MILLIS)) },
        exitTransition = { fadeOut(tween(TAB_FADE_MILLIS)) },
        popEnterTransition = { fadeIn(tween(TAB_FADE_MILLIS)) },
        popExitTransition = { fadeOut(tween(TAB_FADE_MILLIS)) },
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
