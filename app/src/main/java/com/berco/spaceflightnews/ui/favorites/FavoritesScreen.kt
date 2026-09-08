package com.berco.spaceflightnews.ui.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.berco.spaceflightnews.R
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.berco.spaceflightnews.core.model.Article
import com.berco.spaceflightnews.core.ui.DateFormatter
import com.berco.spaceflightnews.core.ui.readableWidth
import com.berco.spaceflightnews.core.ui.OrganicIcons
import com.berco.spaceflightnews.core.ui.component.ArticleCard
import com.berco.spaceflightnews.core.ui.component.StatusView

@Composable
fun FavoritesScreen(
    onArticleClick: (Long) -> Unit,
    onBrowseFeed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dateFormatter = remember { DateFormatter() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .readableWidth()
            .windowInsetsPadding(WindowInsets.statusBars),
    ) {
        FavoritesHeader(
            savedCount = (uiState as? FavoritesUiState.Saved)?.articles?.size ?: 0,
        )

        when (val state = uiState) {
            FavoritesUiState.Loading -> Unit

            FavoritesUiState.Empty -> StatusView(
                icon = OrganicIcons.HeartOutline,
                title = stringResource(R.string.favorites_empty_title),
                message = stringResource(R.string.favorites_empty_message),
                discColor = MaterialTheme.colorScheme.secondaryContainer,
                iconTint = MaterialTheme.colorScheme.onSecondaryContainer,
                actionLabel = stringResource(R.string.favorites_empty_action),
                actionIcon = OrganicIcons.ChevronRight,
                onAction = onBrowseFeed,
            )

            is FavoritesUiState.Saved -> SavedList(
                articles = state.articles,
                dateFormatter = dateFormatter,
                onArticleClick = onArticleClick,
                onToggleFavorite = viewModel::onToggleFavorite,
            )
        }
    }
}

@Composable
private fun FavoritesHeader(savedCount: Int) {
    Column(Modifier.padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 14.dp)) {
        Text(stringResource(R.string.favorites_title), style = MaterialTheme.typography.displaySmall)
        if (savedCount > 0) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = pluralStringResource(R.plurals.favorites_saved_count, savedCount, savedCount),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SavedList(
    articles: List<Article>,
    dateFormatter: DateFormatter,
    onArticleClick: (Long) -> Unit,
    onToggleFavorite: (Article) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        items(items = articles, key = { it.id }, contentType = { "article" }) { article ->
            ArticleCard(
                article = article,
                dateLabel = dateFormatter.format(article.publishedAt),
                onClick = { onArticleClick(article.id) },
                onToggleFavorite = { onToggleFavorite(article) },
            )
        }
    }
}
