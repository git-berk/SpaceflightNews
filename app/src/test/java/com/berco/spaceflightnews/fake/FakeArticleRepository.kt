package com.berco.spaceflightnews.fake

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.berco.spaceflightnews.core.data.repository.ArticleRepository
import com.berco.spaceflightnews.core.model.Article
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

/**
 * Backed by a real [Pager] rather than `PagingData.from`, whose static load
 * states never settle and leave `asSnapshot` waiting forever behind `cachedIn`.
 */
class FakeArticleRepository : ArticleRepository {

    val searchQueries = mutableListOf<String>()

    var feedArticles: List<Article> = listOf(article(1), article(2))
    var searchArticles: List<Article> = listOf(article(3))

    override fun feed(): Flow<PagingData<Article>> = pagerOf { feedArticles }

    override fun search(query: String): Flow<PagingData<Article>> {
        searchQueries += query
        return pagerOf { searchArticles }
    }

    override suspend fun getArticle(id: Long): Article? = feedArticles.find { it.id == id }

    private fun pagerOf(items: () -> List<Article>) = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { ListPagingSource(items()) },
    ).flow

    private class ListPagingSource(
        private val items: List<Article>,
    ) : PagingSource<Int, Article>() {

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
            val offset = params.key ?: 0
            val page = items.drop(offset).take(params.loadSize)
            return LoadResult.Page(
                data = page,
                prevKey = null,
                nextKey = (offset + page.size).takeIf { it < items.size },
            )
        }

        override fun getRefreshKey(state: PagingState<Int, Article>): Int? = null
    }

    companion object {
        fun article(id: Long) = Article(
            id = id,
            title = "Article $id",
            summary = "Summary $id",
            imageUrl = null,
            newsSite = "NASASpaceflight",
            authors = listOf("Author $id"),
            url = "https://example.com/$id",
            publishedAt = Instant.parse("2026-09-05T09:41:57Z"),
        )
    }
}
