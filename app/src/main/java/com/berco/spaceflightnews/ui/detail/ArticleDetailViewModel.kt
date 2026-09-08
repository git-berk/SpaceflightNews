package com.berco.spaceflightnews.ui.detail

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.berco.spaceflightnews.core.data.repository.ArticleRepository
import com.berco.spaceflightnews.core.data.repository.FavoriteRepository
import com.berco.spaceflightnews.core.model.Article
import com.berco.spaceflightnews.ui.detail.navigation.ArticleDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
sealed interface ArticleDetailUiState {
    data object Loading : ArticleDetailUiState

    data class Content(val article: Article) : ArticleDetailUiState

    /** The article is neither cached nor reachable, so there is nothing to show. */
    data object NotFound : ArticleDetailUiState
}

/** The read on its own, before favourite state is layered on. */
private sealed interface ArticleLoad {
    data object Pending : ArticleLoad
    data object Missing : ArticleLoad
    data class Ready(val article: Article) : ArticleLoad
}

@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
    private val favoriteRepository: FavoriteRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val articleId: Long = savedStateHandle.toRoute<ArticleDetailRoute>().articleId

    /**
     * Hot, so the read survives the screen going away and coming back. A cold
     * `flow { }` here would re-run its builder on every resubscription, which for
     * an uncached article means another network call.
     */
    private val article = MutableStateFlow<ArticleLoad>(ArticleLoad.Pending)

    init {
        loadArticle()
    }

    /**
     * Favourite state is the one input that keeps changing, so it stays a stream.
     * Combining rather than writing into a shared state means a favourite emitted
     * while the read is still in flight cannot be lost.
     */
    val uiState: StateFlow<ArticleDetailUiState> =
        combine(article, favoriteRepository.observeFavoriteIds()) { load, favoriteIds ->
            when (load) {
                ArticleLoad.Pending -> ArticleDetailUiState.Loading
                ArticleLoad.Missing -> ArticleDetailUiState.NotFound
                is ArticleLoad.Ready -> ArticleDetailUiState.Content(
                    load.article.copy(isFavorite = articleId in favoriteIds),
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT),
            initialValue = ArticleDetailUiState.Loading,
        )

    private fun loadArticle() {
        viewModelScope.launch {
            val loaded = articleRepository.getArticle(articleId)
            article.value = when (loaded) {
                null -> ArticleLoad.Missing
                else -> ArticleLoad.Ready(loaded)
            }
        }
    }

    fun onToggleFavorite(article: Article) {
        viewModelScope.launch { favoriteRepository.toggle(article) }
    }

    private companion object {
        const val STOP_TIMEOUT = 5_000L
    }
}
