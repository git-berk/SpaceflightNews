package com.berco.spaceflightnews.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.berco.spaceflightnews.R
import com.berco.spaceflightnews.core.ui.DateFormatter
import com.berco.spaceflightnews.core.ui.OrganicIcons
import com.berco.spaceflightnews.core.ui.component.DetailPane
import com.berco.spaceflightnews.core.ui.component.StatusView
import com.berco.spaceflightnews.core.ui.readableWidth
import com.berco.spaceflightnews.ui.util.openArticle
import com.berco.spaceflightnews.ui.util.shareArticle

@Composable
fun ArticleDetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ArticleDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val toolbarColor = MaterialTheme.colorScheme.surface.toArgb()
    val dateFormatter = remember { DateFormatter() }

    when (val state = uiState) {
        ArticleDetailUiState.Loading -> Surface(modifier) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }

        ArticleDetailUiState.NotFound -> Surface(modifier) {
            StatusView(
                icon = OrganicIcons.Alert,
                title = stringResource(R.string.detail_not_found_title),
                message = stringResource(R.string.detail_not_found_message),
                actionLabel = stringResource(R.string.detail_not_found_action),
                actionIcon = OrganicIcons.ArrowLeft,
                onAction = onBack,
                modifier = Modifier.readableWidth(),
            )
        }

        is ArticleDetailUiState.Content -> DetailPane(
            article = state.article,
            dateLabel = dateFormatter.format(state.article.publishedAt),
            onBack = onBack,
            onToggleFavorite = { viewModel.onToggleFavorite(state.article) },
            onShare = { context.shareArticle(state.article) },
            onReadFullArticle = { context.openArticle(state.article, toolbarColor) },
            modifier = modifier,
        )
    }
}

/** Both non-content states fill the window on the article's own background. */
@Composable
private fun Surface(modifier: Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
