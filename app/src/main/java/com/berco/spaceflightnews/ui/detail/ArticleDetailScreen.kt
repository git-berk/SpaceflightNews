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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.berco.spaceflightnews.core.ui.DateFormatter
import com.berco.spaceflightnews.core.ui.component.DetailPane
import com.berco.spaceflightnews.ui.util.openArticle
import com.berco.spaceflightnews.ui.util.shareArticle

@Composable
fun ArticleDetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ArticleDetailViewModel = hiltViewModel(),
) {
    val article by viewModel.article.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val toolbarColor = MaterialTheme.colorScheme.surface.toArgb()
    val dateFormatter = remember { DateFormatter() }

    when (val current = article) {
        null -> Box(
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }

        else -> DetailPane(
            article = current,
            dateLabel = dateFormatter.format(current.publishedAt),
            onBack = onBack,
            onToggleFavorite = { viewModel.onToggleFavorite(current) },
            onShare = { context.shareArticle(current) },
            onReadFullArticle = { context.openArticle(current, toolbarColor) },
            modifier = modifier,
        )
    }
}
