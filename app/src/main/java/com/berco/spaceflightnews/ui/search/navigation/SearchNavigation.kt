package com.berco.spaceflightnews.ui.search.navigation

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
    composable<SearchRoute> {
        SearchScreen(onArticleClick = onArticleClick, onBack = onBack)
    }
}
