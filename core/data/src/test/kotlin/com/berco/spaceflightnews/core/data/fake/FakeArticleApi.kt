package com.berco.spaceflightnews.core.data.fake

import com.berco.spaceflightnews.core.data.remote.ArticleApi
import com.berco.spaceflightnews.core.data.remote.dto.ArticleDto
import com.berco.spaceflightnews.core.data.remote.dto.AuthorDto
import com.berco.spaceflightnews.core.data.remote.dto.PagedResponseDto
import java.io.IOException

class FakeArticleApi : ArticleApi {

    data class Call(val limit: Int, val offset: Int, val publishedAtLte: String?)

    val calls = mutableListOf<Call>()
    var totalAvailable = 100
    var failWith: Exception? = null

    /** Reproduces an API that advertises another page while returning none. */
    var alwaysReportNext = false

    override suspend fun getArticles(
        limit: Int,
        offset: Int,
        publishedAtLte: String?,
        ordering: String,
    ): PagedResponseDto<ArticleDto> {
        calls += Call(limit, offset, publishedAtLte)
        failWith?.let { throw it }
        return page(limit, offset)
    }

    override suspend fun searchArticles(
        query: String,
        limit: Int,
        offset: Int,
        publishedAtLte: String?,
        ordering: String,
    ): PagedResponseDto<ArticleDto> {
        calls += Call(limit, offset, publishedAtLte)
        failWith?.let { throw it }
        return page(limit, offset)
    }

    override suspend fun getArticle(id: Long): ArticleDto {
        failWith?.let { throw it }
        return article(id)
    }

    private fun page(limit: Int, offset: Int): PagedResponseDto<ArticleDto> {
        val ids = (offset until minOf(offset + limit, totalAvailable)).map { it.toLong() }
        val consumed = offset + ids.size
        return PagedResponseDto(
            count = totalAvailable,
            next = if (!alwaysReportNext && consumed >= totalAvailable) null else "next",
            previous = null,
            results = ids.map(::article),
        )
    }

    private fun article(id: Long) = ArticleDto(
        id = id,
        title = "Article $id",
        url = "https://example.com/$id",
        newsSite = "NASASpaceflight",
        publishedAt = "2026-09-05T09:41:57Z",
        summary = "Summary $id",
        authors = listOf(AuthorDto("Author $id")),
        imageUrl = "https://example.com/$id.jpg",
    )

    companion object {
        fun offline() = IOException("offline")
    }
}
