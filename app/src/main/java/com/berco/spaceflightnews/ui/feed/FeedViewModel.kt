package com.berco.spaceflightnews.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.berco.spaceflightnews.core.data.repository.ArticleRepository
import com.berco.spaceflightnews.core.data.repository.FavoriteRepository
import com.berco.spaceflightnews.core.model.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    articleRepository: ArticleRepository,
    private val favoriteRepository: FavoriteRepository,
) : ViewModel() {

    /**
     * `cachedIn` runs before `combine` so toggling a favourite re-maps the
     * cached pages instead of invalidating the pager and refreshing it, which
     * would collapse the loaded window and shift the list.
     */
    val feed: Flow<PagingData<Article>> = articleRepository.feed()
        .cachedIn(viewModelScope)
        .combine(favoriteRepository.observeFavoriteIds()) { paging, ids ->
            paging.map { it.copy(isFavorite = it.id in ids) }
        }

    fun onToggleFavorite(article: Article) {
        viewModelScope.launch { favoriteRepository.toggle(article) }
    }
}
