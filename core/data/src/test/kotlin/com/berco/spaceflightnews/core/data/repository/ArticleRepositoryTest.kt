package com.berco.spaceflightnews.core.data.repository

import androidx.room.Room
import com.berco.spaceflightnews.core.data.fake.FakeArticleApi
import com.berco.spaceflightnews.core.data.fake.fixedClock
import com.berco.spaceflightnews.core.data.local.SpaceflightDatabase
import com.berco.spaceflightnews.core.data.local.entity.ArticleEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import kotlin.time.Instant

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ArticleRepositoryTest {

    private lateinit var db: SpaceflightDatabase
    private lateinit var api: FakeArticleApi
    private lateinit var repository: ArticleRepository

    private val now = Instant.parse("2026-09-05T12:00:00Z")

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            RuntimeEnvironment.getApplication(),
            SpaceflightDatabase::class.java,
        ).allowMainThreadQueries().build()
        api = FakeArticleApi()
        repository = ArticleRepositoryImpl(api, db, fixedClock(now))
    }

    @After
    fun tearDown() {
        if (db.isOpen) db.close()
    }

    @Test
    fun `a cached article is served without touching the network`() = runTest {
        db.articleDao().upsertAll(listOf(cached()))

        val article = repository.getArticle(7)

        assertEquals("Cached", article?.title)
        assertEquals(0, api.calls.size)
    }

    @Test
    fun `an unknown article falls through to the network`() = runTest {
        val article = repository.getArticle(7)

        assertEquals(7L, article?.id)
    }

    @Test
    fun `a network failure yields null rather than throwing`() = runTest {
        api.failWith = FakeArticleApi.offline()

        assertNull(repository.getArticle(7))
    }

    @Test
    fun `a failing database read yields null rather than escaping the caller`() = runTest {
        // Dropping the table produces a real SQLiteException. Closing the database
        // instead would raise a CancellationException, which must keep propagating.
        db.openHelper.writableDatabase.execSQL("DROP TABLE articles")

        assertNull(repository.getArticle(7))
    }

    private fun cached() = ArticleEntity(
        id = 7,
        title = "Cached",
        summary = "From a previous session",
        imageUrl = null,
        newsSite = "NASA",
        authors = emptyList(),
        url = "https://example.com/7",
        publishedAtMillis = now.toEpochMilliseconds(),
    )
}
