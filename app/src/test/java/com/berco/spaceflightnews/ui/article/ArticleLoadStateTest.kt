package com.berco.spaceflightnews.ui.article

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.LoadStates
import com.berco.spaceflightnews.core.model.AppError
import com.berco.spaceflightnews.core.model.AppException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

private val IDLE = LoadState.NotLoading(endOfPaginationReached = false)
private val END = LoadState.NotLoading(endOfPaginationReached = true)
private val LOADING = LoadState.Loading
private val ERROR = LoadState.Error(RuntimeException("boom"))
private val RATE_LIMITED = LoadState.Error(AppException(AppError.RateLimited))

private fun states(
    refresh: LoadState = IDLE,
    append: LoadState = IDLE,
) = LoadStates(refresh = refresh, prepend = IDLE, append = append)

private fun combined(
    sourceRefresh: LoadState = IDLE,
    sourceAppend: LoadState = IDLE,
    mediatorRefresh: LoadState? = null,
    mediatorAppend: LoadState = IDLE,
): CombinedLoadStates {
    val source = states(sourceRefresh, sourceAppend)
    val mediator = mediatorRefresh?.let { states(it, mediatorAppend) }
    return CombinedLoadStates(
        refresh = mediator?.refresh ?: source.refresh,
        prepend = IDLE,
        append = mediator?.append ?: source.append,
        source = source,
        mediator = mediator,
    )
}

class ArticleLoadStateTest {

    @Test
    fun `mediator loading marks a refresh`() {
        assertTrue(combined(mediatorRefresh = LOADING).toArticleLoadState(0).isRefreshing)
    }

    @Test
    fun `source loading marks a refresh when the mediator is idle`() {
        // Inside the cache TTL the mediator skips its refresh, so only Room reports work.
        val state = combined(sourceRefresh = LOADING, mediatorRefresh = IDLE)

        assertTrue(state.toArticleLoadState(0).isRefreshing)
    }

    @Test
    fun `an idle feed is not refreshing`() {
        assertFalse(combined(mediatorRefresh = IDLE).toArticleLoadState(20).isRefreshing)
    }

    @Test
    fun `a mediator error surfaces as a refresh error`() {
        assertNotNull(combined(mediatorRefresh = ERROR).toArticleLoadState(0).refreshError)
    }

    @Test
    fun `search reports a refresh error through the combined state`() {
        // Search has no mediator at all, so the fallback is what surfaces the failure.
        val state = combined(sourceRefresh = ERROR, mediatorRefresh = null)

        assertNotNull(state.toArticleLoadState(0).refreshError)
    }

    @Test
    fun `a source error is ignored while the mediator reports success`() {
        val state = combined(sourceRefresh = ERROR, mediatorRefresh = IDLE)

        assertNull(state.toArticleLoadState(20).refreshError)
    }

    @Test
    fun `a rate limited refresh keeps its type instead of reading as offline`() {
        val state = combined(sourceRefresh = RATE_LIMITED, mediatorRefresh = null)

        assertEquals(AppError.RateLimited, state.toArticleLoadState(0).refreshError)
    }

    @Test
    fun `an unwrapped failure is reported as unknown rather than guessed at`() {
        val error = combined(mediatorRefresh = ERROR).toArticleLoadState(0).refreshError

        assertTrue(error is AppError.Unknown)
    }

    @Test
    fun `emptiness follows the item count`() {
        assertTrue(combined().toArticleLoadState(0).isEmpty)
        assertFalse(combined().toArticleLoadState(1).isEmpty)
    }

    @Test
    fun `append loading and error map across`() {
        assertEquals(
            ArticleLoadState.AppendState.Loading,
            combined(sourceAppend = LOADING).toArticleLoadState(20).append,
        )
        assertEquals(
            ArticleLoadState.AppendState.Error,
            combined(sourceAppend = ERROR).toArticleLoadState(20).append,
        )
    }

    @Test
    fun `end of pagination on a populated list reports the end`() {
        assertEquals(
            ArticleLoadState.AppendState.EndReached,
            combined(sourceAppend = END).toArticleLoadState(20).append,
        )
    }

    @Test
    fun `end of pagination on an empty list stays idle`() {
        // Guards the footer from rendering as the only row on an empty feed.
        assertEquals(
            ArticleLoadState.AppendState.Idle,
            combined(sourceAppend = END).toArticleLoadState(0).append,
        )
    }

    @Test
    fun `an append that is merely idle reports idle`() {
        assertEquals(
            ArticleLoadState.AppendState.Idle,
            combined(sourceAppend = IDLE).toArticleLoadState(20).append,
        )
    }
}
