package com.berco.spaceflightnews.ui.detail.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.berco.spaceflightnews.ui.detail.ArticleDetailRoute

const val ARTICLE_ID_ARG = "articleId"
const val ARTICLE_DETAIL_ROUTE = "article_detail_route"

private const val SLIDE_DURATION_MILLIS = 300

fun NavController.navigateToArticleDetail(articleId: Long) {
    navigate("$ARTICLE_DETAIL_ROUTE/$articleId")
}

fun NavGraphBuilder.articleDetailScreen(
    onBack: () -> Unit,
) {
    composable(
        route = "$ARTICLE_DETAIL_ROUTE/{$ARTICLE_ID_ARG}",
        arguments = listOf(navArgument(ARTICLE_ID_ARG) { type = NavType.LongType }),
        // Detail slides over the list from the right and returns the same way,
        // so the gesture and the motion agree about which direction is "back".
        enterTransition = {
            slideInHorizontally(
                animationSpec = tween(SLIDE_DURATION_MILLIS, easing = FastOutSlowInEasing),
                initialOffsetX = { fullWidth -> fullWidth },
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                animationSpec = tween(SLIDE_DURATION_MILLIS, easing = FastOutSlowInEasing),
                targetOffsetX = { fullWidth -> fullWidth },
            )
        },
    ) {
        ArticleDetailRoute(onBack = onBack)
    }
}
