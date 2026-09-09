package com.berco.spaceflightnews.ui.search.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.berco.spaceflightnews.ui.search.SearchScreen
import kotlinx.serialization.Serializable

@Serializable
data object SearchRoute

fun NavController.navigateToSearch() {
    navigate(SearchRoute)
}

fun NavGraphBuilder.searchScreen(
    onArticleClick: (Long) -> Unit,
    onBack: () -> Unit,
) {
    // The design swaps the top bar in place rather than sliding a new screen in.
    composable<SearchRoute>(
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        SearchScreen(onArticleClick = onArticleClick, onBack = onBack)
    }
}
