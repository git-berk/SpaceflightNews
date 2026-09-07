package com.berco.spaceflightnews.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berco.spaceflightnews.core.data.repository.ArticleRepository
import com.berco.spaceflightnews.core.data.repository.FavoriteRepository
import com.berco.spaceflightnews.core.model.Article
import androidx.navigation.toRoute
import com.berco.spaceflightnews.ui.detail.navigation.ArticleDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    articleRepository: ArticleRepository,
    private val favoriteRepository: FavoriteRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val articleId: Long = savedStateHandle.toRoute<ArticleDetailRoute>().articleId

    /**
     * The article is read once; favourite state is layered on separately so
     * toggling it does not re-read the article.
     */
    val article: StateFlow<Article?> = combine(
        flow { emit(articleRepository.getArticle(articleId)) },
        favoriteRepository.observeFavoriteIds(),
    ) { article, favoriteIds ->
        article?.copy(isFavorite = article.id in favoriteIds)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT), null)

    fun onToggleFavorite(article: Article) {
        viewModelScope.launch { favoriteRepository.toggle(article) }
    }

    private companion object {
        const val STOP_TIMEOUT = 5_000L
    }
}
