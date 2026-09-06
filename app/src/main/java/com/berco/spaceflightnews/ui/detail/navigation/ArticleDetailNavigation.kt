package com.berco.spaceflightnews.ui.detail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.berco.spaceflightnews.ui.detail.ArticleDetailRoute

const val ARTICLE_ID_ARG = "articleId"
const val ARTICLE_DETAIL_ROUTE = "article_detail_route"

fun NavController.navigateToArticleDetail(articleId: Long) {
    navigate("$ARTICLE_DETAIL_ROUTE/$articleId")
}

fun NavGraphBuilder.articleDetailScreen(
    onBack: () -> Unit,
) {
    composable(
        route = "$ARTICLE_DETAIL_ROUTE/{$ARTICLE_ID_ARG}",
        arguments = listOf(navArgument(ARTICLE_ID_ARG) { type = NavType.LongType }),
    ) {
        ArticleDetailRoute(onBack = onBack)
    }
}
