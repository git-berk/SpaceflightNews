package com.berco.spaceflightnews.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berco.spaceflightnews.core.data.repository.ArticleRepository
import com.berco.spaceflightnews.core.data.repository.FavoriteRepository
import com.berco.spaceflightnews.core.model.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
    private val favoriteRepository: FavoriteRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val selectedId = savedStateHandle.getStateFlow<Long?>(KEY_ID, null)

    /**
     * The article is fetched once per selection; favourite state is layered on
     * separately so toggling it does not re-read the article.
     */
    val article: StateFlow<Article?> = combine(
        selectedId.flatMapLatest { id -> flow { emit(id?.let { articleRepository.getArticle(it) }) } },
        favoriteRepository.observeFavoriteIds(),
    ) { article, favoriteIds ->
        article?.copy(isFavorite = article.id in favoriteIds)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT), null)

    fun select(id: Long?) {
        savedStateHandle[KEY_ID] = id
    }

    fun onToggleFavorite(article: Article) {
        viewModelScope.launch { favoriteRepository.toggle(article) }
    }

    private companion object {
        const val KEY_ID = "detail.articleId"
        const val STOP_TIMEOUT = 5_000L
    }
}
