package com.berco.spaceflightnews.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.berco.spaceflightnews.R
import com.berco.spaceflightnews.core.ui.OrganicIcons
import com.berco.spaceflightnews.ui.favorites.navigation.FavoritesRoute
import com.berco.spaceflightnews.ui.feed.navigation.FeedRoute

enum class TopLevelDestination(
    val route: Any,
    @param:StringRes val labelRes: Int,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
) {
    FEED(FeedRoute, R.string.nav_feed, OrganicIcons.Newspaper, OrganicIcons.Newspaper),
    FAVORITES(FavoritesRoute, R.string.nav_favorites, OrganicIcons.HeartOutline, OrganicIcons.HeartFilled),
}

fun NavDestination?.toTopLevelDestination(): TopLevelDestination? =
    TopLevelDestination.entries.firstOrNull { destination ->
        this?.hierarchy?.any { it.hasRoute(destination.route::class) } == true
    }

/**
 * Tabs swap rather than stack: each keeps its own back stack and saved state,
 * and re-selecting one returns to its root instead of pushing a duplicate.
 */
fun NavController.navigateToTopLevel(destination: TopLevelDestination) {
    navigate(destination.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
