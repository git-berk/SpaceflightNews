package com.berco.spaceflightnews.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.berco.spaceflightnews.core.data.mapper.asAppException
import com.berco.spaceflightnews.core.data.mapper.toDomain
import com.berco.spaceflightnews.core.data.remote.ArticleApi
import com.berco.spaceflightnews.core.model.Article
import java.time.Clock
import java.time.Instant

/**
 * Search results are transient, so they go straight from the network to the UI
 * with no Room layer. The trade-off is that search has no offline fallback.
 */
class SearchArticlePagingSource(
    private val api: ArticleApi,
    private val query: String,
    clock: Clock,
) : PagingSource<Int, Article>() {

    /**
     * Paging builds a new source per generation, so this pins the result set for
     * one scroll. Without it an article published mid-scroll shifts every later
     * offset and a page re-serves an id the list is already showing, which
     * crashes LazyColumn on the duplicate key.
     */
    private val snapshotIso = Instant.now(clock).toString()

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val offset = params.key ?: 0
        return try {
            val response = api.searchArticles(
                query = query,
                limit = params.loadSize,
                offset = offset,
                publishedAtLte = snapshotIso,
            )
            LoadResult.Page(
                data = response.results.map { it.toDomain() },
                prevKey = null,
                nextKey = if (response.next == null || response.results.isEmpty()) {
                    null
                } else {
                    offset + response.results.size
                },
            )
        } catch (e: Exception) {
            LoadResult.Error(e.asAppException())
        }
    }

    /** Null restarts at offset 0, which is what refreshing a search should do. */
    override fun getRefreshKey(state: PagingState<Int, Article>): Int? = null
}
