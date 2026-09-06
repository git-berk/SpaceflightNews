package com.berco.spaceflightnews.core.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.berco.spaceflightnews.core.data.local.SpaceflightDatabase
import com.berco.spaceflightnews.core.data.local.entity.ArticleWithFavorite
import com.berco.spaceflightnews.core.data.local.entity.RemoteKeyEntity
import com.berco.spaceflightnews.core.data.mapper.asAppException
import com.berco.spaceflightnews.core.data.mapper.toEntity
import com.berco.spaceflightnews.core.data.remote.ArticleApi
import java.time.Clock
import java.time.Instant
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalPagingApi::class)
class ArticleRemoteMediator(
    private val api: ArticleApi,
    private val db: SpaceflightDatabase,
    private val clock: Clock = Clock.systemUTC(),
) : RemoteMediator<Int, ArticleWithFavorite>() {

    private val articleDao = db.articleDao()
    private val keyDao = db.remoteKeyDao()

    /**
     * Without this the Pager refreshes on every creation, wiping the cache on
     * each cold start. The window matches the API's own `cache-control:
     * max-age=600` — refreshing sooner cannot return newer data.
     */
    override suspend fun initialize(): InitializeAction {
        val lastRefreshed = keyDao.get()?.lastRefreshedAtMillis
            ?: return InitializeAction.LAUNCH_INITIAL_REFRESH

        val age = clock.millis() - lastRefreshed
        return if (age < CACHE_TTL_MILLIS) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleWithFavorite>,
    ): MediatorResult {
        return try {
            val (offset, snapshot) = when (loadType) {
                LoadType.REFRESH -> 0 to Instant.now(clock).toString()

                // Newest-first feed; there is never anything above the first page.
                LoadType.PREPEND ->
                    return MediatorResult.Success(endOfPaginationReached = true)

                LoadType.APPEND -> {
                    val key = keyDao.get()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    val next = key.nextOffset
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    next to key.snapshotIso
                }
            }

            // Runs before any delete: a failed refresh must leave the cache intact.
            val response = api.getArticles(
                limit = PAGE_SIZE,
                offset = offset,
                publishedAtLte = snapshot,
            )

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    articleDao.clearAll()
                    keyDao.clear()
                }
                articleDao.upsertAll(response.results.map { it.toEntity() })
                keyDao.upsert(
                    RemoteKeyEntity(
                        nextOffset = if (response.next == null) {
                            null
                        } else {
                            offset + response.results.size
                        },
                        snapshotIso = snapshot,
                        lastRefreshedAtMillis = clock.millis(),
                    ),
                )
            }

            MediatorResult.Success(endOfPaginationReached = response.next == null)
        } catch (e: Exception) {
            MediatorResult.Error(e.asAppException())
        }
    }

    companion object {
        const val PAGE_SIZE = 20
        val CACHE_TTL_MILLIS = 10.minutes.inWholeMilliseconds
    }
}
