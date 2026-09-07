package com.berco.spaceflightnews.core.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import com.berco.spaceflightnews.core.data.fake.FakeArticleApi
import com.berco.spaceflightnews.core.data.local.SpaceflightDatabase
import com.berco.spaceflightnews.core.data.local.entity.ArticleEntity
import com.berco.spaceflightnews.core.data.local.entity.RemoteKeyEntity
import com.berco.spaceflightnews.core.model.AppError
import com.berco.spaceflightnews.core.model.AppException
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

@OptIn(ExperimentalPagingApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ArticleRemoteMediatorTest {

    private lateinit var db: SpaceflightDatabase
    private lateinit var api: FakeArticleApi

    private val now = Instant.parse("2026-09-05T12:00:00Z")

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            RuntimeEnvironment.getApplication(),
            SpaceflightDatabase::class.java,
        ).allowMainThreadQueries().build()
        api = FakeArticleApi()
    }

    @After
    fun tearDown() = db.close()

    private fun mediator(at: Instant = now) =
        ArticleRemoteMediator(api, db, Clock.fixed(at, ZoneOffset.UTC))

    private fun emptyState() = PagingState<Int, ArticleEntity>(
        pages = emptyList(),
        anchorPosition = null,
        config = PagingConfig(pageSize = ArticleRemoteMediator.PAGE_SIZE),
        leadingPlaceholderCount = 0,
    )

    @Test
    fun `refresh inserts the first page and records pagination state`() = runTest {
        val result = mediator().load(LoadType.REFRESH, emptyState())

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertEquals(ArticleRemoteMediator.PAGE_SIZE, db.articleDao().count())

        val key = db.remoteKeyDao().get()!!
        assertEquals(ArticleRemoteMediator.PAGE_SIZE, key.nextOffset)
        assertEquals(now.toString(), key.snapshotIso)
    }

    @Test
    fun `refresh pins a snapshot so later pages cannot drift`() = runTest {
        val mediator = mediator()
        mediator.load(LoadType.REFRESH, emptyState())
        mediator.load(LoadType.APPEND, emptyState())

        assertEquals(2, api.calls.size)
        assertEquals(now.toString(), api.calls[0].publishedAtLte)
        assertEquals(now.toString(), api.calls[1].publishedAtLte)
        assertEquals(0, api.calls[0].offset)
        assertEquals(ArticleRemoteMediator.PAGE_SIZE, api.calls[1].offset)
    }

    @Test
    fun `refresh replaces stale rows`() = runTest {
        db.articleDao().upsertAll(listOf(staleArticle()))

        mediator().load(LoadType.REFRESH, emptyState())

        assertNull(db.articleDao().getById(999))
        assertEquals(ArticleRemoteMediator.PAGE_SIZE, db.articleDao().count())
    }

    @Test
    fun `a failed refresh leaves the cached feed intact`() = runTest {
        db.articleDao().upsertAll(listOf(staleArticle()))
        api.failWith = FakeArticleApi.offline()

        val result = mediator().load(LoadType.REFRESH, emptyState())

        assertTrue(result is RemoteMediator.MediatorResult.Error)
        assertEquals(1, db.articleDao().count())
        assertEquals("Cached", db.articleDao().getById(999)?.title)
    }

    @Test
    fun `failures surface as a mapped AppError`() = runTest {
        api.failWith = FakeArticleApi.offline()

        val result = mediator().load(LoadType.REFRESH, emptyState())
        val error = (result as RemoteMediator.MediatorResult.Error).throwable

        assertTrue(error is AppException)
        assertEquals(AppError.Network, (error as AppException).error)
    }

    @Test
    fun `pagination ends when the API reports no next page`() = runTest {
        api.totalAvailable = ArticleRemoteMediator.PAGE_SIZE

        val result = mediator().load(LoadType.REFRESH, emptyState())

        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        assertNull(db.remoteKeyDao().get()?.nextOffset)
    }

    @Test
    fun `an empty page ends pagination even if the API reports a next link`() = runTest {
        api.totalAvailable = 0
        api.alwaysReportNext = true

        val result = mediator().load(LoadType.REFRESH, emptyState())

        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        assertNull(db.remoteKeyDao().get()?.nextOffset)
    }

    @Test
    fun `prepend is a no-op on a newest-first feed`() = runTest {
        val result = mediator().load(LoadType.PREPEND, emptyState())

        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        assertTrue(api.calls.isEmpty())
    }

    @Test
    fun `append without stored keys does not hit the network`() = runTest {
        val result = mediator().load(LoadType.APPEND, emptyState())

        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        assertTrue(api.calls.isEmpty())
    }

    @Test
    fun `appending does not extend the refresh TTL`() = runTest {
        val started = now.minusMillis(ArticleRemoteMediator.CACHE_TTL_MILLIS - 1_000)
        seedKey(refreshedAt = started)

        mediator().load(LoadType.APPEND, emptyState())

        assertEquals(started.toEpochMilli(), db.remoteKeyDao().get()!!.lastRefreshedAtMillis)
    }

    @Test
    fun `a long scroll session still refreshes on the next cold start`() = runTest {
        val started = now.minusMillis(ArticleRemoteMediator.CACHE_TTL_MILLIS - 1_000)
        seedKey(refreshedAt = started)
        mediator().load(LoadType.APPEND, emptyState())

        val later = started.plusMillis(ArticleRemoteMediator.CACHE_TTL_MILLIS + 1)

        assertEquals(
            RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH,
            mediator(at = later).initialize(),
        )
    }

    @Test
    fun `appending carries the original snapshot forward`() = runTest {
        val started = now.minusMillis(1_000)
        seedKey(refreshedAt = started)

        mediator().load(LoadType.APPEND, emptyState())

        assertEquals(started.toString(), api.calls.single().publishedAtLte)
    }

    @Test
    fun `initialize skips refresh inside the TTL window`() = runTest {
        seedKey(refreshedAt = now.minusMillis(ArticleRemoteMediator.CACHE_TTL_MILLIS - 1))

        assertEquals(
            RemoteMediator.InitializeAction.SKIP_INITIAL_REFRESH,
            mediator().initialize(),
        )
    }

    @Test
    fun `initialize refreshes once the TTL has lapsed`() = runTest {
        seedKey(refreshedAt = now.minusMillis(ArticleRemoteMediator.CACHE_TTL_MILLIS + 1))

        assertEquals(
            RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH,
            mediator().initialize(),
        )
    }

    @Test
    fun `initialize refreshes when nothing has been cached`() = runTest {
        assertEquals(
            RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH,
            mediator().initialize(),
        )
    }

    private suspend fun seedKey(refreshedAt: Instant) = db.remoteKeyDao().upsert(
        RemoteKeyEntity(
            nextOffset = 20,
            snapshotIso = refreshedAt.toString(),
            lastRefreshedAtMillis = refreshedAt.toEpochMilli(),
        ),
    )

    private fun staleArticle() = ArticleEntity(
        id = 999,
        title = "Cached",
        summary = "From a previous session",
        imageUrl = null,
        newsSite = "NASA",
        authors = emptyList(),
        url = "https://example.com/999",
        publishedAtMillis = now.toEpochMilli(),
    )
}
