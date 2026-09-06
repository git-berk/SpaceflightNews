package com.berco.spaceflightnews.ui.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.berco.spaceflightnews.core.model.Article
import com.berco.spaceflightnews.core.ui.DateFormatter
import com.berco.spaceflightnews.core.ui.OrganicIcons
import com.berco.spaceflightnews.core.ui.component.ArticleCard
import com.berco.spaceflightnews.core.ui.component.ArticleRow
import com.berco.spaceflightnews.core.ui.component.StatusView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    selectedId: Long?,
    isTwoPane: Boolean,
    onArticleClick: (Long) -> Unit,
    onBrowseFeed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val dateFormatter = remember { DateFormatter() }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            LargeTopAppBar(
                title = { Text("Favorites", style = MaterialTheme.typography.displaySmall) },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
    ) { contentPadding ->
        when (val state = uiState) {
            FavoritesUiState.Loading -> Unit

            FavoritesUiState.Empty -> StatusView(
                icon = OrganicIcons.HeartOutline,
                title = "Nothing saved yet",
                message = "Tap the heart on any story and it will wait for you here — " +
                    "no connection needed.",
                discColor = MaterialTheme.colorScheme.secondaryContainer,
                iconTint = MaterialTheme.colorScheme.onSecondaryContainer,
                actionLabel = "Browse the feed",
                actionIcon = OrganicIcons.ChevronRight,
                onAction = onBrowseFeed,
                modifier = Modifier.padding(contentPadding),
            )

            is FavoritesUiState.Saved -> SavedList(
                articles = state.articles,
                selectedId = selectedId,
                isTwoPane = isTwoPane,
                dateFormatter = dateFormatter,
                contentPadding = contentPadding,
                onArticleClick = onArticleClick,
                onToggleFavorite = viewModel::onToggleFavorite,
            )
        }
    }
}

@Composable
private fun SavedList(
    articles: List<Article>,
    selectedId: Long?,
    isTwoPane: Boolean,
    dateFormatter: DateFormatter,
    contentPadding: PaddingValues,
    onArticleClick: (Long) -> Unit,
    onToggleFavorite: (Article) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = contentPadding.calculateTopPadding() + 8.dp,
            bottom = contentPadding.calculateBottomPadding() + 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(if (isTwoPane) 4.dp else 20.dp),
    ) {
        items(items = articles, key = { it.id }, contentType = { "article" }) { article ->
            val dateLabel = dateFormatter.format(article.publishedAt)
            if (isTwoPane) {
                ArticleRow(
                    article = article,
                    dateLabel = dateLabel,
                    selected = article.id == selectedId,
                    onClick = { onArticleClick(article.id) },
                )
            } else {
                ArticleCard(
                    article = article,
                    dateLabel = dateLabel,
                    onClick = { onArticleClick(article.id) },
                    onToggleFavorite = { onToggleFavorite(article) },
                )
            }
        }
    }
}
