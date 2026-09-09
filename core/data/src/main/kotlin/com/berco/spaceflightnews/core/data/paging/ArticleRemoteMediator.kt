package com.berco.spaceflightnews.core.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.berco.spaceflightnews.core.data.local.SpaceflightDatabase
import com.berco.spaceflightnews.core.data.local.entity.ArticleEntity
import com.berco.spaceflightnews.core.data.local.entity.RemoteKeyEntity
import com.berco.spaceflightnews.core.data.mapper.asAppException
import com.berco.spaceflightnews.core.data.mapper.toEntity
import com.berco.spaceflightnews.core.data.remote.ArticleApi
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalPagingApi::class)
class ArticleRemoteMediator(
    private val api: ArticleApi,
    private val db: SpaceflightDatabase,
    private val clock: Clock = Clock.System,
) : RemoteMediator<Int, ArticleEntity>() {

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

        val age = clock.now().toEpochMilliseconds() - lastRefreshed
        return if (age < CACHE_TTL_MILLIS) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleEntity>,
    ): MediatorResult {
        return try {
            val load = when (loadType) {
                LoadType.REFRESH -> LoadKey(
                    offset = 0,
                    snapshotIso = clock.now().toString(),
                    refreshedAtMillis = clock.now().toEpochMilliseconds(),
                )

                // Newest-first feed; there is never anything above the first page.
                LoadType.PREPEND ->
                    return MediatorResult.Success(endOfPaginationReached = true)

                LoadType.APPEND -> {
                    val key = keyDao.get()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    val next = key.nextOffset
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    // Carried forward, not restamped: the TTL measures time since
                    // the last refresh, not since the last page.
                    LoadKey(next, key.snapshotIso, key.lastRefreshedAtMillis)
                }
            }

            // Paging asks for initialLoadSize up front and pageSize thereafter.
            val limit = when (loadType) {
                LoadType.REFRESH -> state.config.initialLoadSize
                else -> state.config.pageSize
            }

            // Runs before any delete: a failed refresh must leave the cache intact.
            val response = api.getArticles(
                limit = limit,
                offset = load.offset,
                publishedAtLte = load.snapshotIso,
            )

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    articleDao.clearAll()
                    keyDao.clear()
                }
                articleDao.upsertAll(response.results.map { it.toEntity() })
                keyDao.upsert(
                    RemoteKeyEntity(
                        nextOffset = if (response.next == null || response.results.isEmpty()) {
                            null
                        } else {
                            load.offset + response.results.size
                        },
                        snapshotIso = load.snapshotIso,
                        lastRefreshedAtMillis = load.refreshedAtMillis,
                    ),
                )
            }

            MediatorResult.Success(
                endOfPaginationReached = response.next == null || response.results.isEmpty(),
            )
        } catch (e: Exception) {
            MediatorResult.Error(e.asAppException())
        }
    }

    private data class LoadKey(
        val offset: Int,
        val snapshotIso: String,
        val refreshedAtMillis: Long,
    )

    companion object {
        const val PAGE_SIZE = 20
        val CACHE_TTL_MILLIS = 10.minutes.inWholeMilliseconds
    }
}
