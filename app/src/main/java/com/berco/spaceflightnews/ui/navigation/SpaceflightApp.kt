package com.berco.spaceflightnews.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowWidthSizeClass
import com.berco.spaceflightnews.core.ui.OrganicIcons
import com.berco.spaceflightnews.core.ui.component.BottomNavItem
import com.berco.spaceflightnews.core.ui.component.OrganicBottomNav
import com.berco.spaceflightnews.core.ui.theme.OrganicColors
import com.berco.spaceflightnews.ui.favorites.FavoritesScreen
import com.berco.spaceflightnews.ui.feed.FeedScreen

private const val TAB_FEED = "feed"
private const val TAB_FAVORITES = "favorites"

@Composable
fun SpaceflightApp() {
    var selectedTab by rememberSaveable { mutableStateOf(TAB_FEED) }

    val destinations = remember {
        listOf(
            BottomNavItem(TAB_FEED, "Feed", OrganicIcons.Newspaper),
            BottomNavItem(
                key = TAB_FAVORITES,
                label = "Favorites",
                icon = OrganicIcons.HeartOutline,
                selectedIcon = OrganicIcons.HeartFilled,
            ),
        )
    }

    // The design swaps the bottom bar for a rail once both panes are visible.
    val useRail = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass ==
        WindowWidthSizeClass.EXPANDED

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        // The screens inside own their status-bar inset through their top bars;
        // applying it here too would double the gap above the title.
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (!useRail) {
                OrganicBottomNav(
                    items = destinations,
                    selectedKey = selectedTab,
                    onSelect = { selectedTab = it },
                )
            }
        },
    ) { contentPadding ->
        Row(
            Modifier
                .fillMaxSize()
                .padding(bottom = contentPadding.calculateBottomPadding())
                .consumeWindowInsets(contentPadding),
        ) {
            if (useRail) {
                NavigationRail(containerColor = MaterialTheme.colorScheme.surfaceContainerLow) {
                    destinations.forEach { item ->
                        val selected = item.key == selectedTab
                        NavigationRailItem(
                            selected = selected,
                            onClick = { selectedTab = item.key },
                            icon = {
                                Icon(
                                    if (selected) item.selectedIcon else item.icon,
                                    contentDescription = null,
                                )
                            },
                            label = { Text(item.label, style = MaterialTheme.typography.labelMedium) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                indicatorColor = OrganicColors.Accent200,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )
                    }
                }
            }

            Box(Modifier.fillMaxSize()) {
                when (selectedTab) {
                    TAB_FAVORITES -> ArticleListDetail { selectedId, isTwoPane, onArticleClick ->
                        FavoritesScreen(
                            selectedId = selectedId,
                            isTwoPane = isTwoPane,
                            onArticleClick = onArticleClick,
                            onBrowseFeed = { selectedTab = TAB_FEED },
                        )
                    }

                    else -> ArticleListDetail { selectedId, isTwoPane, onArticleClick ->
                        FeedScreen(
                            selectedId = selectedId,
                            isTwoPane = isTwoPane,
                            onArticleClick = onArticleClick,
                        )
                    }
                }
            }
        }
    }
}
