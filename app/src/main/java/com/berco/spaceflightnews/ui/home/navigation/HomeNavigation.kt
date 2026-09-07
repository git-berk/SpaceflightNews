package com.berco.spaceflightnews.ui.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.berco.spaceflightnews.ui.home.HomeScreen

const val HOME_ROUTE = "home_route"

fun NavGraphBuilder.homeScreen(
    onArticleClick: (Long) -> Unit,
) {
    composable(route = HOME_ROUTE) {
        HomeScreen(onArticleClick = onArticleClick)
    }
}
