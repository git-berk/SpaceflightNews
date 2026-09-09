package com.berco.spaceflightnews.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.berco.spaceflightnews.core.data.repository.ArticleRepository
import com.berco.spaceflightnews.core.data.repository.FavoriteRepository
import com.berco.spaceflightnews.core.model.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/** Shared with the screen, which must not show "no results" for a short query. */
internal const val MIN_QUERY_LENGTH = 2

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val articleRepository: ArticleRepository,
    private val favoriteRepository: FavoriteRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val query: StateFlow<String> = savedStateHandle.getStateFlow(KEY_QUERY, "")

    /** Network-only, so favourite state is layered on after the fact. */
    val results: Flow<PagingData<Article>> = query
        .map { it.trim() }
        .distinctUntilChanged()
        .transformLatest { text ->
            if (text.length < MIN_QUERY_LENGTH) {
                emit(PagingData.empty())
            } else {
                // Announced before the debounce so the screen shows skeletons
                // while keystrokes settle. Emitting nothing here would leave the
                // previous page on screen, and an empty one reads as "no results".
                emit(PagingData.empty(SEARCH_PENDING))
                delay(SEARCH_DEBOUNCE_MILLIS.milliseconds)
                emitAll(articleRepository.search(text))
            }
        }
        .cachedIn(viewModelScope)
        .combine(favoriteRepository.observeFavoriteIds()) { paging, ids ->
            paging.map { it.copy(isFavorite = it.id in ids) }
        }

    fun onQueryChange(value: String) {
        savedStateHandle[KEY_QUERY] = value
    }

    fun onToggleFavorite(article: Article) {
        viewModelScope.launch { favoriteRepository.toggle(article) }
    }

    private companion object {
        /** transformLatest cancels the pending delay, so this debounces. */
        val SEARCH_PENDING = LoadStates(
            refresh = LoadState.Loading,
            prepend = LoadState.NotLoading(endOfPaginationReached = true),
            append = LoadState.NotLoading(endOfPaginationReached = true),
        )

        const val KEY_QUERY = "search.query"
        const val SEARCH_DEBOUNCE_MILLIS = 1_000L
    }
}
