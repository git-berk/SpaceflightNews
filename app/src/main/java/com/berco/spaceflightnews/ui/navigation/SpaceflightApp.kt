package com.berco.spaceflightnews.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.berco.spaceflightnews.core.ui.component.OrganicBottomNav

@Composable
fun SpaceflightApp() {
    val navController = rememberNavController()
    val currentRoute by navController.currentBackStackEntryAsState()

    // The bar belongs to the top-level destinations only, so pushing the detail
    // screen hides it without anything having to coordinate.
    val showBottomBar = currentRoute?.destination?.route.isTopLevelRoute()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        // Each destination owns its status-bar inset through its own top bar.
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (showBottomBar) {
                OrganicBottomNav(
                    items = topLevelDestinations,
                    selectedKey = currentRoute?.destination?.route ?: topLevelDestinations.first().key,
                    onSelect = navController::navigateToTopLevel,
                )
            }
        },
    ) { contentPadding ->
        SpaceflightNavHost(
            navController = navController,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = contentPadding.calculateBottomPadding())
                .consumeWindowInsets(contentPadding),
        )
    }
}
