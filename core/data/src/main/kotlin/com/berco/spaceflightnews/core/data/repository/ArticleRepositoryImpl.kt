package com.berco.spaceflightnews.core.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.berco.spaceflightnews.core.data.local.SpaceflightDatabase
import com.berco.spaceflightnews.core.data.mapper.toDomain
import com.berco.spaceflightnews.core.data.paging.ArticleRemoteMediator
import com.berco.spaceflightnews.core.data.paging.ArticleRemoteMediator.Companion.PAGE_SIZE
import com.berco.spaceflightnews.core.data.paging.SearchArticlePagingSource
import com.berco.spaceflightnews.core.data.remote.ArticleApi
import com.berco.spaceflightnews.core.model.Article
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Clock
import javax.inject.Inject

class ArticleRepositoryImpl @Inject constructor(
    private val api: ArticleApi,
    private val db: SpaceflightDatabase,
    private val clock: Clock,
) : ArticleRepository {

    private val articleDao = db.articleDao()
    private val favoriteDao = db.favoriteDao()

    @OptIn(ExperimentalPagingApi::class)
    override fun feed(): Flow<PagingData<Article>> = Pager(
        config = pagingConfig(),
        remoteMediator = ArticleRemoteMediator(api, db, clock),
        pagingSourceFactory = { articleDao.pagingSource() },
    ).flow.map { paging -> paging.map { it.toDomain() } }

    override fun search(query: String): Flow<PagingData<Article>> = Pager(
        config = pagingConfig(),
        pagingSourceFactory = { SearchArticlePagingSource(api, query) },
    ).flow

    /**
     * initialLoadSize defaults to three pages, which would cost three
     * sequential requests before the first frame.
     */
    private fun pagingConfig() = PagingConfig(
        pageSize = PAGE_SIZE,
        initialLoadSize = PAGE_SIZE,
        enablePlaceholders = false,
    )

    override suspend fun getArticle(id: Long): Article? =
        favoriteDao.getById(id)?.toDomain()
            ?: articleDao.getById(id)?.toDomain()
            ?: fetchRemote(id)

    private suspend fun fetchRemote(id: Long): Article? = try {
        api.getArticle(id).toDomain(favoriteDao.isFavorite(id))
    } catch (e: CancellationException) {
        throw e
    } catch (_: Exception) {
        null
    }
}
