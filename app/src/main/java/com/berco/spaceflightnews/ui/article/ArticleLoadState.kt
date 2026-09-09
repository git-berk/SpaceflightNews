package com.berco.spaceflightnews.ui.article

import androidx.compose.runtime.Immutable
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.berco.spaceflightnews.core.model.AppError
import com.berco.spaceflightnews.core.model.AppException

@Immutable
data class ArticleLoadState(
    val isRefreshing: Boolean = false,
    val refreshError: AppError? = null,
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
fun CombinedLoadStates.toArticleLoadState(itemCount: Int): ArticleLoadState {
    val mediatorRefresh = mediator?.refresh
    val appendState = append

    return ArticleLoadState(
        isRefreshing = mediatorRefresh is LoadState.Loading || source.refresh is LoadState.Loading,
        refreshError = ((mediatorRefresh ?: refresh) as? LoadState.Error)?.error?.asAppError(),
        isEmpty = itemCount == 0,
        append = when (appendState) {
            is LoadState.Loading -> ArticleLoadState.AppendState.Loading
            is LoadState.Error -> ArticleLoadState.AppendState.Error
            is LoadState.NotLoading ->
                if (appendState.endOfPaginationReached && itemCount > 0) {
                    ArticleLoadState.AppendState.EndReached
                } else {
                    ArticleLoadState.AppendState.Idle
                }
        },
    )
}

/**
 * Distinct from `core:data`'s `toAppError`, which maps Retrofit and IO failures.
 * By the time a throwable reaches Paging it is already an [AppException]; if it
 * is not, it never passed through the data layer's mapper and is reported as-is.
 */
private fun Throwable.asAppError(): AppError =
    (this as? AppException)?.error ?: AppError.Unknown(this)
