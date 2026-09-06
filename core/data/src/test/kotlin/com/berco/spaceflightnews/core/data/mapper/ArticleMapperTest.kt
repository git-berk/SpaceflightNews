package com.berco.spaceflightnews.core.data.mapper

import com.berco.spaceflightnews.core.data.remote.dto.ArticleDto
import com.berco.spaceflightnews.core.data.remote.dto.AuthorDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class ArticleMapperTest {

    private fun dto(
        publishedAt: String = "2026-09-05T09:41:57Z",
        summary: String = "A summary",
        authors: List<AuthorDto> = listOf(AuthorDto("Justin Davenport")),
        imageUrl: String? = "https://example.com/a.jpg",
    ) = ArticleDto(
        id = 1,
        title = "Title",
        url = "https://example.com",
        newsSite = "NASASpaceflight",
        publishedAt = publishedAt,
        summary = summary,
        authors = authors,
        imageUrl = imageUrl,
    )

    @Test
    fun `maps a well formed article`() {
        val article = dto().toDomain()

        assertEquals(1L, article.id)
        assertEquals("NASASpaceflight", article.newsSite)
        assertEquals(listOf("Justin Davenport"), article.authors)
        assertEquals(Instant.parse("2026-09-05T09:41:57Z"), article.publishedAt)
    }

    @Test
    fun `epoch stub dates become null`() {
        assertNull(dto(publishedAt = "1970-01-01T00:00:00Z").toDomain().publishedAt)
    }

    @Test
    fun `dates before 2000 become null`() {
        assertNull(dto(publishedAt = "1999-12-31T23:59:59Z").toDomain().publishedAt)
    }

    @Test
    fun `unparseable dates become null rather than throwing`() {
        assertNull(dto(publishedAt = "not-a-date").toDomain().publishedAt)
    }

    @Test
    fun `empty summary and authors survive mapping`() {
        val article = dto(summary = "", authors = emptyList()).toDomain()

        assertEquals("", article.summary)
        assertTrue(article.authors.isEmpty())
    }

    @Test
    fun `blank image url becomes null`() {
        assertNull(dto(imageUrl = "   ").toDomain().imageUrl)
        assertNull(dto(imageUrl = null).toDomain().imageUrl)
    }

    @Test
    fun `entity round trip preserves the domain model`() {
        val original = dto().toDomain()
        val restored = dto().toEntity().toDomain()

        assertEquals(original, restored)
    }

    @Test
    fun `favourite entity keeps the article and is always favourite`() {
        val favorite = dto().toDomain().toFavoriteEntity(savedAtMillis = 42L).toDomain()

        assertEquals("Title", favorite.title)
        assertTrue(favorite.isFavorite)
    }
}
