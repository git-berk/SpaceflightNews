package com.berco.spaceflightnews.ui.detail.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.berco.spaceflightnews.ui.detail.ArticleDetailScreen
import kotlinx.serialization.Serializable

private const val SLIDE_DURATION_MILLIS = 300

@Serializable
data class ArticleDetailRoute(val articleId: Long)

fun NavController.navigateToArticleDetail(articleId: Long) {
    navigate(ArticleDetailRoute(articleId))
}

fun NavGraphBuilder.articleDetailScreen(
    onBack: () -> Unit,
) {
    composable<ArticleDetailRoute>(
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
        ArticleDetailScreen(onBack = onBack)
    }
}
