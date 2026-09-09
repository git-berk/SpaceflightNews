package com.berco.spaceflightnews.ui.feed

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.berco.spaceflightnews.core.model.Article
import com.berco.spaceflightnews.core.ui.readableWidth
import com.berco.spaceflightnews.ui.article.PagedArticles
import com.berco.spaceflightnews.ui.article.toArticleLoadState

@Composable
fun FeedScreen(
    onArticleClick: (Long) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = hiltViewModel(),
) {
    FeedContent(
        items = viewModel.feed.collectAsLazyPagingItems(),
        onArticleClick = onArticleClick,
        onSearchClick = onSearchClick,
        onToggleFavorite = viewModel::onToggleFavorite,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedContent(
    items: LazyPagingItems<Article>,
    onArticleClick: (Long) -> Unit,
    onSearchClick: () -> Unit,
    onToggleFavorite: (Article) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val listState = rememberLazyListState()
    val loadState = items.loadState.toArticleLoadState(items.itemCount)

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            FeedTopBar(
                modifier = Modifier.readableWidth(),
                scrollBehavior = scrollBehavior,
                onSearchClick = onSearchClick,
            )
        },
    ) { contentPadding ->
        PullToRefreshBox(
            isRefreshing = loadState.isRefreshing && !loadState.isEmpty,
            onRefresh = items::refresh,
            modifier = Modifier
                .fillMaxSize()
                .readableWidth()
                .padding(contentPadding),
        ) {
            PagedArticles(
                items = items,
                loadState = loadState,
                listState = listState,
                onArticleClick = onArticleClick,
                onToggleFavorite = onToggleFavorite,
            )
        }
    }
}
