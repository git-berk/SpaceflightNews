package com.berco.spaceflightnews.ui.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.berco.spaceflightnews.ui.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavGraphBuilder.homeScreen(
    onArticleClick: (Long) -> Unit,
) {
    composable<HomeRoute> {
        HomeScreen(onArticleClick = onArticleClick)
    }
}
