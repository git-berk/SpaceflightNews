package com.berco.spaceflightnews.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berco.spaceflightnews.core.data.repository.FavoriteRepository
import com.berco.spaceflightnews.core.model.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FavoritesUiState {
    data object Loading : FavoritesUiState
    data object Empty : FavoritesUiState
    data class Saved(val articles: List<Article>) : FavoritesUiState
}

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
) : ViewModel() {

    val uiState: StateFlow<FavoritesUiState> = favoriteRepository.observeFavorites()
        .map { articles ->
            if (articles.isEmpty()) FavoritesUiState.Empty else FavoritesUiState.Saved(articles)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT), FavoritesUiState.Loading)

    fun onToggleFavorite(article: Article) {
        viewModelScope.launch { favoriteRepository.toggle(article) }
    }

    private companion object {
        const val STOP_TIMEOUT = 5_000L
    }
}
