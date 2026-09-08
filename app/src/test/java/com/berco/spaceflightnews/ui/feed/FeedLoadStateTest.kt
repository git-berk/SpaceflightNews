package com.berco.spaceflightnews.ui.feed

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.LoadStates
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

private val IDLE = LoadState.NotLoading(endOfPaginationReached = false)
private val END = LoadState.NotLoading(endOfPaginationReached = true)
private val LOADING = LoadState.Loading
private val ERROR = LoadState.Error(RuntimeException("boom"))

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

class FeedLoadStateTest {

    @Test
    fun `mediator loading marks a refresh`() {
        assertTrue(combined(mediatorRefresh = LOADING).toFeedLoadState(0).isRefreshing)
    }

    @Test
    fun `source loading marks a refresh when the mediator is idle`() {
        // Inside the cache TTL the mediator skips its refresh, so only Room reports work.
        val state = combined(sourceRefresh = LOADING, mediatorRefresh = IDLE)

        assertTrue(state.toFeedLoadState(0).isRefreshing)
    }

    @Test
    fun `an idle feed is not refreshing`() {
        assertFalse(combined(mediatorRefresh = IDLE).toFeedLoadState(20).isRefreshing)
    }

    @Test
    fun `a mediator error surfaces as a refresh error`() {
        assertTrue(combined(mediatorRefresh = ERROR).toFeedLoadState(0).hasRefreshError)
    }

    @Test
    fun `search reports a refresh error through the combined state`() {
        // Search has no mediator at all, so the fallback is what surfaces the failure.
        val state = combined(sourceRefresh = ERROR, mediatorRefresh = null)

        assertTrue(state.toFeedLoadState(0).hasRefreshError)
    }

    @Test
    fun `a source error is ignored while the mediator reports success`() {
        val state = combined(sourceRefresh = ERROR, mediatorRefresh = IDLE)

        assertFalse(state.toFeedLoadState(20).hasRefreshError)
    }

    @Test
    fun `emptiness follows the item count`() {
        assertTrue(combined().toFeedLoadState(0).isEmpty)
        assertFalse(combined().toFeedLoadState(1).isEmpty)
    }

    @Test
    fun `append loading and error map across`() {
        assertEquals(
            FeedLoadState.AppendState.Loading,
            combined(sourceAppend = LOADING).toFeedLoadState(20).append,
        )
        assertEquals(
            FeedLoadState.AppendState.Error,
            combined(sourceAppend = ERROR).toFeedLoadState(20).append,
        )
    }

    @Test
    fun `end of pagination on a populated list reports the end`() {
        assertEquals(
            FeedLoadState.AppendState.EndReached,
            combined(sourceAppend = END).toFeedLoadState(20).append,
        )
    }

    @Test
    fun `end of pagination on an empty list stays idle`() {
        // Guards the footer from rendering as the only row on an empty feed.
        assertEquals(
            FeedLoadState.AppendState.Idle,
            combined(sourceAppend = END).toFeedLoadState(0).append,
        )
    }

    @Test
    fun `an append that is merely idle reports idle`() {
        assertEquals(
            FeedLoadState.AppendState.Idle,
            combined(sourceAppend = IDLE).toFeedLoadState(20).append,
        )
    }
}
