package com.berco.spaceflightnews.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.berco.spaceflightnews.core.ui.OrganicIcons
import com.berco.spaceflightnews.core.ui.component.BottomNavItem
import com.berco.spaceflightnews.ui.favorites.navigation.FAVORITES_ROUTE
import com.berco.spaceflightnews.ui.feed.navigation.FEED_ROUTE

val topLevelDestinations = listOf(
    BottomNavItem(FEED_ROUTE, "Feed", OrganicIcons.Newspaper),
    BottomNavItem(
        key = FAVORITES_ROUTE,
        label = "Favorites",
        icon = OrganicIcons.HeartOutline,
        selectedIcon = OrganicIcons.HeartFilled,
    ),
)

/**
 * Tabs swap rather than stack: each keeps its own back stack and saved state,
 * and re-selecting one returns to its root instead of pushing a duplicate.
 */
fun NavController.navigateToTopLevel(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
