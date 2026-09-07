package com.berco.spaceflightnews.ui.feed

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.berco.spaceflightnews.core.data.repository.ArticleRepository
import com.berco.spaceflightnews.core.data.repository.FavoriteRepository
import com.berco.spaceflightnews.core.model.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/** Shared with the screen, which must not show "no results" for a short query. */
internal const val MIN_QUERY_LENGTH = 2

data class FeedUiState(
    val query: String = "",
    val isSearchActive: Boolean = false,
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class FeedViewModel @Inject constructor(
    articleRepository: ArticleRepository,
    private val favoriteRepository: FavoriteRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val query = savedStateHandle.getStateFlow(KEY_QUERY, "")

    val uiState: StateFlow<FeedUiState> = combine(
        query,
        savedStateHandle.getStateFlow(KEY_SEARCH_ACTIVE, false),
    ) { text, active -> FeedUiState(text, active) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT), FeedUiState())

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

    /** Network-only, so favourite state is layered on the same way. */
    val searchResults: Flow<PagingData<Article>> = query
        .debounce(SEARCH_DEBOUNCE_MILLIS.milliseconds)
        .map { it.trim() }
        .distinctUntilChanged()
        .flatMapLatest { text ->
            if (text.length < MIN_QUERY_LENGTH) {
                flowOf(PagingData.empty())
            } else {
                articleRepository.search(text)
            }
        }
        .cachedIn(viewModelScope)
        .combine(favoriteRepository.observeFavoriteIds()) { paging, ids ->
            paging.map { it.copy(isFavorite = it.id in ids) }
        }

    fun onQueryChange(value: String) {
        savedStateHandle[KEY_QUERY] = value
    }

    fun onSearchActiveChange(active: Boolean) {
        savedStateHandle[KEY_SEARCH_ACTIVE] = active
        if (!active) savedStateHandle[KEY_QUERY] = ""
    }

    fun onToggleFavorite(article: Article) {
        viewModelScope.launch { favoriteRepository.toggle(article) }
    }

    private companion object {
        const val KEY_QUERY = "feed.query"
        const val KEY_SEARCH_ACTIVE = "feed.searchActive"
        const val SEARCH_DEBOUNCE_MILLIS = 500L
        const val STOP_TIMEOUT = 5_000L
    }
}
