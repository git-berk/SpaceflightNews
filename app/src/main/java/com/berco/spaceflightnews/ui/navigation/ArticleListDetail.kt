package com.berco.spaceflightnews.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.berco.spaceflightnews.core.ui.DateFormatter
import com.berco.spaceflightnews.core.ui.component.DetailPane
import com.berco.spaceflightnews.ui.detail.ArticleDetailViewModel
import com.berco.spaceflightnews.ui.util.openArticle
import com.berco.spaceflightnews.ui.util.shareArticle
import kotlinx.coroutines.launch

/**
 * One scaffold per tab. On compact widths the detail pane is a full screen with
 * a back arrow; from medium widths up both panes are visible and selecting a
 * row swaps the detail in place, so [DetailPane] is reused unchanged.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ArticleListDetail(
    modifier: Modifier = Modifier,
    detailViewModel: ArticleDetailViewModel = hiltViewModel(),
    listPane: @Composable (selectedId: Long?, isTwoPane: Boolean, onArticleClick: (Long) -> Unit) -> Unit,
) {
    val navigator = rememberListDetailPaneScaffoldNavigator<Long>()
    val scope = rememberCoroutineScope()
    val article by detailViewModel.article.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val toolbarColor = MaterialTheme.colorScheme.surface.toArgb()
    val dateFormatter = remember { DateFormatter() }

    NavigableListDetailPaneScaffold(
        navigator = navigator,
        modifier = modifier,
        listPane = {
            AnimatedPane {
                val isTwoPane = navigator.scaffoldDirective.maxHorizontalPartitions > 1
                listPane(navigator.currentDestination?.contentKey, isTwoPane) { id ->
                    detailViewModel.select(id)
                    scope.launch { navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, id) }
                }
            }
        },
        detailPane = {
            AnimatedPane {
                val current = article
                if (current == null) {
                    EmptyDetailPane()
                } else {
                    DetailPane(
                        article = current,
                        dateLabel = dateFormatter.format(current.publishedAt),
                        onBack = if (navigator.canNavigateBack()) {
                            { scope.launch { navigator.navigateBack() } }
                        } else {
                            null
                        },
                        onToggleFavorite = { detailViewModel.onToggleFavorite(current) },
                        onShare = { context.shareArticle(current) },
                        onReadFullArticle = { context.openArticle(current, toolbarColor) },
                    )
                }
            }
        },
    )
}

@Composable
private fun EmptyDetailPane() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            "Select a story to read",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
