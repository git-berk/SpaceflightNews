package com.berco.spaceflightnews.ui.feed

import androidx.compose.runtime.Immutable
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState

@Immutable
data class FeedLoadState(
    val isRefreshing: Boolean = false,
    val hasRefreshError: Boolean = false,
    val isEmpty: Boolean = true,
    val append: AppendState = AppendState.Idle,
) {
    enum class AppendState { Idle, Loading, Error, EndReached }
}

/**
 * Errors come from the mediator (search has no mediator, so fall back to the
 * combined state), but loading must also account for Room still querying:
 * inside the cache TTL the mediator reports NotLoading immediately.
 */
fun CombinedLoadStates.toFeedLoadState(itemCount: Int): FeedLoadState {
    val mediatorRefresh = mediator?.refresh
    val appendState = append

    return FeedLoadState(
        isRefreshing = mediatorRefresh is LoadState.Loading || source.refresh is LoadState.Loading,
        hasRefreshError = (mediatorRefresh ?: refresh) is LoadState.Error,
        isEmpty = itemCount == 0,
        append = when (appendState) {
            is LoadState.Loading -> FeedLoadState.AppendState.Loading
            is LoadState.Error -> FeedLoadState.AppendState.Error
            is LoadState.NotLoading ->
                if (appendState.endOfPaginationReached && itemCount > 0) {
                    FeedLoadState.AppendState.EndReached
                } else {
                    FeedLoadState.AppendState.Idle
                }
        },
    )
}
