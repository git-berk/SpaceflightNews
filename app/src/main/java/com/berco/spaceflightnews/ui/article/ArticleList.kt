package com.berco.spaceflightnews.ui.article

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.berco.spaceflightnews.R
import com.berco.spaceflightnews.core.model.AppError
import com.berco.spaceflightnews.core.model.Article
import com.berco.spaceflightnews.core.ui.DateFormatter
import com.berco.spaceflightnews.core.ui.OrganicIcons
import com.berco.spaceflightnews.core.ui.component.ArticleCard
import com.berco.spaceflightnews.core.ui.component.EndOfListFooter
import com.berco.spaceflightnews.core.ui.component.InlineErrorRow
import com.berco.spaceflightnews.core.ui.component.SkeletonCard
import com.berco.spaceflightnews.core.ui.component.StatusView

private const val ARTICLE_CONTENT_TYPE = "article"
private const val SKELETON_COUNT = 4

/**
 * Shared by the feed and by search: both present the same paged list and differ
 * only in what they show when a load succeeds with nothing in it.
 */
@Composable
fun PagedArticles(
    items: LazyPagingItems<Article>,
    loadState: ArticleLoadState,
    listState: LazyListState,
    onArticleClick: (Long) -> Unit,
    onToggleFavorite: (Article) -> Unit,
    modifier: Modifier = Modifier,
    emptyContent: @Composable () -> Unit = {},
) {
    when {
        loadState.isRefreshing && loadState.isEmpty -> SkeletonList(modifier)

        loadState.refreshError != null && loadState.isEmpty -> StatusView(
            modifier = modifier,
            icon = OrganicIcons.Alert,
            title = stringResource(loadState.refreshError.titleRes()),
            message = stringResource(loadState.refreshError.messageRes()),
            actionLabel = stringResource(R.string.feed_offline_action),
            actionIcon = OrganicIcons.Refresh,
            onAction = items::retry,
        )

        loadState.isEmpty -> emptyContent()

        else -> ArticleList(
            items = items,
            listState = listState,
            showOfflineBanner = loadState.refreshError != null,
            appendState = loadState.append,
            onArticleClick = onArticleClick,
            onToggleFavorite = onToggleFavorite,
            modifier = modifier,
        )
    }
}

@Composable
private fun ArticleList(
    items: LazyPagingItems<Article>,
    listState: LazyListState,
    showOfflineBanner: Boolean,
    appendState: ArticleLoadState.AppendState,
    onArticleClick: (Long) -> Unit,
    onToggleFavorite: (Article) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dateFormatter = remember { DateFormatter() }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        if (showOfflineBanner) {
            item(key = "offline-banner", contentType = "banner") {
                InlineErrorRow(
                    message = stringResource(R.string.feed_offline_banner),
                    onRetry = items::retry,
                )
            }
        }

        items(
            count = items.itemCount,
            key = items.itemKey { it.id },
            contentType = items.itemContentType { ARTICLE_CONTENT_TYPE },
        ) { index ->
            when (val article = items[index]) {
                // A placeholder position: the row exists in the database but is
                // not in the currently loaded window yet.
                null -> SkeletonCard()

                else -> ArticleCard(
                    article = article,
                    dateLabel = dateFormatter.format(article.publishedAt),
                    onClick = { onArticleClick(article.id) },
                    onToggleFavorite = { onToggleFavorite(article) },
                )
            }
        }

        when (appendState) {
            ArticleLoadState.AppendState.Loading ->
                item(key = "append-loading", contentType = "skeleton") { SkeletonCard() }

            ArticleLoadState.AppendState.Error ->
                item(key = "append-error", contentType = "banner") {
                    InlineErrorRow(stringResource(R.string.feed_append_error), onRetry = items::retry)
                }

            ArticleLoadState.AppendState.EndReached ->
                item(key = "end-of-list", contentType = "footer") { EndOfListFooter() }

            ArticleLoadState.AppendState.Idle -> Unit
        }
    }
}

@Composable
private fun SkeletonList(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        repeat(SKELETON_COUNT) { SkeletonCard(Modifier.fillMaxWidth()) }
    }
}

/**
 * Rate limiting is called out separately: telling a reader to check a working
 * connection is wrong, and inviting an immediate retry makes it worse.
 */
@StringRes
private fun AppError.titleRes(): Int = when (this) {
    AppError.Network -> R.string.feed_offline_title
    AppError.RateLimited -> R.string.feed_rate_limited_title
    else -> R.string.feed_error_title
}

@StringRes
private fun AppError.messageRes(): Int = when (this) {
    AppError.Network -> R.string.feed_offline_message
    AppError.RateLimited -> R.string.feed_rate_limited_message
    else -> R.string.feed_error_message
}
