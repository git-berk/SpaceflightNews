package com.berco.spaceflightnews.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.berco.spaceflightnews.ui.detail.navigation.articleDetailScreen
import com.berco.spaceflightnews.ui.detail.navigation.navigateToArticleDetail
import com.berco.spaceflightnews.ui.home.navigation.HomeRoute
import com.berco.spaceflightnews.ui.home.navigation.homeScreen

@Composable
fun SpaceflightNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        homeScreen(
            onArticleClick = navController::navigateToArticleDetail,
        )
        articleDetailScreen(
            onBack = navController::popBackStack,
        )
    }
}
