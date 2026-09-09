package com.berco.spaceflightnews.ui.search

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.berco.spaceflightnews.R
import com.berco.spaceflightnews.core.model.Article
import com.berco.spaceflightnews.core.ui.OrganicIcons
import com.berco.spaceflightnews.core.ui.component.StatusView
import com.berco.spaceflightnews.core.ui.readableWidth
import com.berco.spaceflightnews.ui.article.PagedArticles
import com.berco.spaceflightnews.ui.article.toArticleLoadState

@Composable
fun SearchScreen(
    onArticleClick: (Long) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val results = viewModel.results.collectAsLazyPagingItems()

    SearchContent(
        query = query,
        results = results,
        onQueryChange = viewModel::onQueryChange,
        onArticleClick = onArticleClick,
        onToggleFavorite = viewModel::onToggleFavorite,
        onBack = onBack,
        modifier = modifier,
    )
}

@Composable
private fun SearchContent(
    query: String,
    results: LazyPagingItems<Article>,
    onQueryChange: (String) -> Unit,
    onArticleClick: (Long) -> Unit,
    onToggleFavorite: (Article) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val loadState = results.loadState.toArticleLoadState(results.itemCount)

    // Each query is a different list, so the previous offset is meaningless.
    LaunchedEffect(query) { listState.scrollToItem(0) }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            SearchTopBar(
                modifier = Modifier.readableWidth(),
                query = query,
                onQueryChange = onQueryChange,
                onDismiss = onBack,
            )
        },
    ) { contentPadding ->
        val content = Modifier
            .fillMaxSize()
            .readableWidth()
            .padding(contentPadding)

        if (query.trim().length < MIN_QUERY_LENGTH) {
            SearchSuggestions(onSelect = onQueryChange, modifier = content)
        } else {
            PagedArticles(
                items = results,
                loadState = loadState,
                listState = listState,
                onArticleClick = onArticleClick,
                onToggleFavorite = onToggleFavorite,
                modifier = content,
                emptyContent = {
                    StatusView(
                        icon = OrganicIcons.Search,
                        title = stringResource(R.string.feed_search_empty_title, query),
                        message = stringResource(R.string.feed_search_empty_message),
                    )
                },
            )
        }
    }
}
