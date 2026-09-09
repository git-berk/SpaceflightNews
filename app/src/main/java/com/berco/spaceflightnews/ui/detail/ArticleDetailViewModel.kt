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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@Immutable
sealed interface ArticleDetailUiState {
    data object Loading : ArticleDetailUiState

    data class Content(val article: Article) : ArticleDetailUiState

    data object NotFound : ArticleDetailUiState
}

@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
    private val favoriteRepository: FavoriteRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val articleId: Long = savedStateHandle.toRoute<ArticleDetailRoute>().articleId

    private val article = MutableStateFlow<Article?>(null)

    private val _uiState = MutableStateFlow<ArticleDetailUiState>(ArticleDetailUiState.Loading)
    val uiState: StateFlow<ArticleDetailUiState> = _uiState.asStateFlow()

    init {
        observeState()
        loadArticle()
    }

    private fun loadArticle() = viewModelScope.launch {
        when (val loaded = articleRepository.getArticle(articleId)) {
            null -> _uiState.value = ArticleDetailUiState.NotFound
            else -> article.value = loaded
        }
    }

    private fun observeState() {
        combine(
            article.filterNotNull(),
            favoriteRepository.observeFavoriteIds(),
        ) { article, favoriteIds ->
            ArticleDetailUiState.Content(article.copy(isFavorite = articleId in favoriteIds))
        }
            .onEach { _uiState.value = it }
            .launchIn(viewModelScope)
    }

    fun onToggleFavorite(article: Article) {
        viewModelScope.launch { favoriteRepository.toggle(article) }
    }
}
