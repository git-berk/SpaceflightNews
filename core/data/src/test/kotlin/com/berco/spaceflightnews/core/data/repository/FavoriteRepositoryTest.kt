package com.berco.spaceflightnews.core.data.repository

import androidx.room.Room
import app.cash.turbine.test
import com.berco.spaceflightnews.core.data.local.SpaceflightDatabase
import com.berco.spaceflightnews.core.data.local.entity.ArticleEntity
import com.berco.spaceflightnews.core.model.Article
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import com.berco.spaceflightnews.core.data.fake.fixedClock
import kotlin.time.Instant

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class FavoriteRepositoryTest {

    private lateinit var db: SpaceflightDatabase
    private lateinit var repository: FavoriteRepository

    private val now = Instant.parse("2026-09-05T12:00:00Z")

    private val article = Article(
        id = 42,
        title = "Isar Aerospace attempts launch of Spectrum rocket",
        summary = "The European private space launch provider…",
        imageUrl = "https://example.com/42.jpg",
        newsSite = "NASASpaceflight",
        authors = listOf("Justin Davenport"),
        url = "https://example.com/42",
        publishedAt = now,
    )

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            RuntimeEnvironment.getApplication(),
            SpaceflightDatabase::class.java,
        ).allowMainThreadQueries().build()
        repository = FavoriteRepositoryImpl(db.favoriteDao(), fixedClock(now))
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun `toggle saves then removes`() = runTest {
        repository.toggle(article)
        repository.observeFavoriteIds().test {
            assertEquals(setOf(42L), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        repository.toggle(article)
        repository.observeFavoriteIds().test {
            assertTrue(awaitItem().isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a saved article is returned in full`() = runTest {
        repository.toggle(article)

        repository.observeFavorites().test {
            assertEquals(article.copy(isFavorite = true), awaitItem().single())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a favourite survives the feed cache being cleared`() = runTest {
        db.articleDao().upsertAll(listOf(cachedArticle()))
        repository.toggle(article)

        db.articleDao().clearAll()

        assertEquals(0, db.articleDao().count())
        repository.observeFavorites().test {
            assertEquals(1, awaitItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `favourite ids emit on every change`() = runTest {
        repository.observeFavoriteIds().test {
            assertEquals(emptySet<Long>(), awaitItem())

            repository.toggle(article)
            assertEquals(setOf(42L), awaitItem())

            repository.toggle(article)
            assertEquals(emptySet<Long>(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun cachedArticle() = ArticleEntity(
        id = 42,
        title = article.title,
        summary = article.summary,
        imageUrl = article.imageUrl,
        newsSite = article.newsSite,
        authors = article.authors,
        url = article.url,
        publishedAtMillis = now.toEpochMilliseconds(),
    )
}
