package com.berco.spaceflightnews.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.berco.spaceflightnews.core.data.mapper.asAppException
import com.berco.spaceflightnews.core.data.mapper.toDomain
import com.berco.spaceflightnews.core.data.remote.ArticleApi
import com.berco.spaceflightnews.core.model.Article

/**
 * Search results are transient, so they go straight from the network to the UI
 * with no Room layer. The trade-off is that search has no offline fallback.
 */
class SearchArticlePagingSource(
    private val api: ArticleApi,
    private val query: String,
) : PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val offset = params.key ?: 0
        return try {
            val response = api.searchArticles(
                query = query,
                limit = params.loadSize,
                offset = offset,
            )
            LoadResult.Page(
                data = response.results.map { it.toDomain() },
                prevKey = null,
                nextKey = if (response.next == null) null else offset + response.results.size,
            )
        } catch (e: Exception) {
            LoadResult.Error(e.asAppException())
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? =
        state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(state.config.pageSize)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(state.config.pageSize)
        }
}
