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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
sealed interface ArticleDetailUiState {
    data object Loading : ArticleDetailUiState

    data class Content(val article: Article) : ArticleDetailUiState

    /** The article is neither cached nor reachable, so there is nothing to show. */
    data object NotFound : ArticleDetailUiState
}

@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
    private val favoriteRepository: FavoriteRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val articleId: Long = savedStateHandle.toRoute<ArticleDetailRoute>().articleId

    private val _uiState = MutableStateFlow<ArticleDetailUiState>(ArticleDetailUiState.Loading)
    val uiState: StateFlow<ArticleDetailUiState> = _uiState.asStateFlow()

    /** Null until the first emission, which is not the same as "no favourites". */
    private var favoriteIds: Set<Long>? = null

    init {
        loadArticle()
        observeFavorite()
    }

    /**
     * Read once. Held in state rather than rebuilt from a flow, so returning to
     * the screen after a spell in the background does not re-read it — which for
     * an uncached article means another network call.
     */
    private fun loadArticle() {
        viewModelScope.launch {
            val article = articleRepository.getArticle(articleId)
            _uiState.value = when (article) {
                null -> ArticleDetailUiState.NotFound
                // The observer may have emitted while this read was in flight; its
                // answer wins. Falling back to the repository's own value keeps the
                // first frame right when nothing has been observed yet.
                else -> ArticleDetailUiState.Content(article.withFavorite(favoriteIds))
            }
        }
    }

    /** Favourite state is the one part that keeps changing after the read. */
    private fun observeFavorite() {
        viewModelScope.launch {
            favoriteRepository.observeFavoriteIds().collect { ids ->
                favoriteIds = ids
                _uiState.update { state ->
                    when (state) {
                        is ArticleDetailUiState.Content ->
                            state.copy(article = state.article.withFavorite(ids))

                        else -> state
                    }
                }
            }
        }
    }

    fun onToggleFavorite(article: Article) {
        viewModelScope.launch { favoriteRepository.toggle(article) }
    }

    private fun Article.withFavorite(ids: Set<Long>?): Article =
        if (ids == null) this else copy(isFavorite = id in ids)
}
