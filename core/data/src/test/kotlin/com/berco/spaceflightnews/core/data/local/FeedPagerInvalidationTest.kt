package com.berco.spaceflightnews.core.data.local

import androidx.paging.PagingSource
import androidx.room.Room
import com.berco.spaceflightnews.core.data.local.entity.ArticleEntity
import com.berco.spaceflightnews.core.data.local.entity.FavoriteArticleEntity
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.concurrent.Executors

/**
 * Joining favourites into the feed query made Room observe that table, so every
 * favourite toggle invalidated the pager and forced a refresh that collapsed the
 * loaded window and shifted the list. The positive case is asserted alongside so
 * the negative ones cannot pass simply because invalidation never runs.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class FeedPagerInvalidationTest {

    private lateinit var db: SpaceflightDatabase

    @Before
    fun setUp() {
        // The invalidation tracker runs its refresh on the query executor, so it
        // needs a real one rather than Robolectric's paused main looper.
        db = Room.inMemoryDatabaseBuilder(
            RuntimeEnvironment.getApplication(),
            SpaceflightDatabase::class.java,
        )
            .setQueryExecutor(Executors.newSingleThreadExecutor())
            .setTransactionExecutor(Executors.newSingleThreadExecutor())
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun `writing an article invalidates the feed pager`() {
        assertTrue(invalidatedBy { db.articleDao().upsertAll(listOf(article(2))) })
    }

    @Test
    fun `saving a favourite does not invalidate the feed pager`() {
        assertFalse(invalidatedBy { db.favoriteDao().insert(favorite(1)) })
    }

    @Test
    fun `removing a favourite does not invalidate the feed pager`() {
        runBlocking { db.favoriteDao().insert(favorite(1)) }
        assertFalse(invalidatedBy { db.favoriteDao().delete(1) })
    }

    private fun invalidatedBy(write: suspend () -> Unit): Boolean = runBlocking {
        db.articleDao().upsertAll(listOf(article(1)))

        val source = db.articleDao().pagingSource()
        source.load(PagingSource.LoadParams.Refresh(null, PAGE, false))

        val invalidated = CompletableDeferred<Unit>()
        source.registerInvalidatedCallback { invalidated.complete(Unit) }

        write()

        withTimeoutOrNull(TIMEOUT_MILLIS) { invalidated.await() } != null
    }

    private fun article(id: Long) = ArticleEntity(
        id = id,
        title = "Article $id",
        summary = "Summary $id",
        imageUrl = null,
        newsSite = "NASASpaceflight",
        authors = emptyList(),
        url = "https://example.com/$id",
        publishedAtMillis = id,
    )

    private fun favorite(id: Long) = FavoriteArticleEntity(
        id = id,
        title = "Article $id",
        summary = "Summary $id",
        imageUrl = null,
        newsSite = "NASASpaceflight",
        authors = emptyList(),
        url = "https://example.com/$id",
        publishedAtMillis = id,
        savedAtMillis = 0L,
    )

    private companion object {
        const val PAGE = 20
        const val TIMEOUT_MILLIS = 3_000L
    }
}
