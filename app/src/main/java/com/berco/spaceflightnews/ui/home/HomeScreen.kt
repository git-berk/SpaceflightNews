package com.berco.spaceflightnews.ui.home

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.berco.spaceflightnews.core.ui.component.OrganicBottomNav
import com.berco.spaceflightnews.ui.favorites.navigation.favoritesScreen
import com.berco.spaceflightnews.ui.feed.navigation.FEED_ROUTE
import com.berco.spaceflightnews.ui.feed.navigation.feedScreen
import com.berco.spaceflightnews.ui.navigation.navigateToTopLevel
import com.berco.spaceflightnews.ui.navigation.topLevelDestinations

private const val TAB_FADE_MILLIS = 200

/**
 * Owns the bottom bar and the tab graph. Article detail is a sibling of this
 * whole container in the parent graph rather than a peer of the tabs, so it
 * covers the bar structurally instead of the bar being switched off for it.
 */
@Composable
fun HomeScreen(
    onArticleClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabController = rememberNavController()
    val currentEntry by tabController.currentBackStackEntryAsState()
    val selectedTab = currentEntry?.destination?.route ?: FEED_ROUTE

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        // Each tab owns its status-bar inset through its own top bar.
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            OrganicBottomNav(
                items = topLevelDestinations,
                selectedKey = selectedTab,
                onSelect = tabController::navigateToTopLevel,
            )
        },
    ) { contentPadding ->
        NavHost(
            navController = tabController,
            startDestination = FEED_ROUTE,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = contentPadding.calculateBottomPadding())
                .consumeWindowInsets(contentPadding),
            // Tabs are siblings, so they cross-fade rather than slide.
            enterTransition = { fadeIn(tween(TAB_FADE_MILLIS)) },
            exitTransition = { fadeOut(tween(TAB_FADE_MILLIS)) },
            popEnterTransition = { fadeIn(tween(TAB_FADE_MILLIS)) },
            popExitTransition = { fadeOut(tween(TAB_FADE_MILLIS)) },
        ) {
            feedScreen(onArticleClick = onArticleClick)
            favoritesScreen(
                onArticleClick = onArticleClick,
                onBrowseFeed = { tabController.navigateToTopLevel(FEED_ROUTE) },
            )
        }
    }
}
