package com.berco.spaceflightnews.ui.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import androidx.compose.material3.TopAppBarDefaults
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

@Composable
fun FeedScreen(
    onArticleClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val feedItems = viewModel.feed.collectAsLazyPagingItems()
    val searchItems = viewModel.searchResults.collectAsLazyPagingItems()

    FeedContent(
        uiState = uiState,
        items = if (uiState.isSearchActive) searchItems else feedItems,
        onArticleClick = onArticleClick,
        onQueryChange = viewModel::onQueryChange,
        onSearchActiveChange = viewModel::onSearchActiveChange,
        onToggleFavorite = viewModel::onToggleFavorite,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedContent(
    uiState: FeedUiState,
    items: LazyPagingItems<Article>,
    onArticleClick: (Long) -> Unit,
    onQueryChange: (String) -> Unit,
    onSearchActiveChange: (Boolean) -> Unit,
    onToggleFavorite: (Article) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val loadState = items.loadState.toFeedLoadState(items.itemCount)
    val isSearchIdle = uiState.isSearchActive && uiState.query.trim().length < MIN_QUERY_LENGTH

    val feedListState = rememberLazyListState()
    val searchListState = rememberLazyListState()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            FeedTopBar(
                uiState = uiState,
                scrollBehavior = scrollBehavior,
                onQueryChange = onQueryChange,
                onSearchActiveChange = onSearchActiveChange,
            )
        },
    ) { contentPadding ->
        PullToRefreshBox(
            isRefreshing = loadState.isRefreshing && !loadState.isEmpty,
            onRefresh = items::refresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        ) {
            when {
                isSearchIdle -> SearchSuggestions(onSelect = onQueryChange)

                loadState.isRefreshing && loadState.isEmpty -> SkeletonList()

                loadState.hasRefreshError && loadState.isEmpty -> StatusView(
                    icon = OrganicIcons.Alert,
                    title = "No connection",
                    message = "We couldn't reach the newsroom. Check your connection and try again.",
                    actionLabel = "Try again",
                    actionIcon = OrganicIcons.Refresh,
                    onAction = items::retry,
                )

                loadState.isEmpty && uiState.isSearchActive -> StatusView(
                    icon = OrganicIcons.Search,
                    title = "No stories for “${uiState.query}”",
                    message = "Try a different keyword, or check the spelling.",
                )

                else -> ArticleList(
                    items = items,
                    listState = if (uiState.isSearchActive) searchListState else feedListState,
                    showOfflineBanner = loadState.hasRefreshError,
                    appendState = loadState.append,
                    onArticleClick = onArticleClick,
                    onToggleFavorite = onToggleFavorite,
                )
            }
        }
    }
}

@Composable
private fun ArticleList(
    items: LazyPagingItems<Article>,
    listState: LazyListState,
    showOfflineBanner: Boolean,
    appendState: FeedLoadState.AppendState,
    onArticleClick: (Long) -> Unit,
    onToggleFavorite: (Article) -> Unit,
) {
    val dateFormatter = remember { DateFormatter() }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        if (showOfflineBanner) {
            item(key = "offline-banner", contentType = "banner") {
                InlineErrorRow(
                    message = "Showing saved stories — couldn't reach the network.",
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
            FeedLoadState.AppendState.Loading ->
                item(key = "append-loading", contentType = "skeleton") { SkeletonCard() }

            FeedLoadState.AppendState.Error ->
                item(key = "append-error", contentType = "banner") {
                    InlineErrorRow("Couldn't load more stories.", onRetry = items::retry)
                }

            FeedLoadState.AppendState.EndReached ->
                item(key = "end-of-list", contentType = "footer") { EndOfListFooter() }

            FeedLoadState.AppendState.Idle -> Unit
        }
    }
}

@Composable
private fun SkeletonList() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        repeat(SKELETON_COUNT) { SkeletonCard(Modifier.fillMaxWidth()) }
    }
}
