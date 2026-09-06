package com.berco.spaceflightnews.core.data.mapper

import com.berco.spaceflightnews.core.data.local.entity.ArticleEntity
import com.berco.spaceflightnews.core.data.local.entity.ArticleWithFavorite
import com.berco.spaceflightnews.core.data.local.entity.FavoriteArticleEntity
import com.berco.spaceflightnews.core.data.remote.dto.ArticleDto
import com.berco.spaceflightnews.core.model.Article
import java.time.Instant

/**
 * The feed contains ~59 records dated before 2000, including 1970 epoch stubs.
 * Anything older than this is treated as "no date" rather than rendered.
 */
private val MIN_PLAUSIBLE_DATE: Instant = Instant.parse("2000-01-01T00:00:00Z")

/**
 * WordPress-sourced feeds append "The post <title> appeared first on <site>."
 * after the real summary. Dropping it keeps the truncating ellipsis meaningful
 * instead of stranding it mid-card next to boilerplate.
 */
private val RSS_TRAILER = Regex(
    """\s*The post\b.*?\bappeared first on\b.*""",
    RegexOption.DOT_MATCHES_ALL,
)

internal fun String.cleanSummary(): String = replace(RSS_TRAILER, "").trim()

internal fun String?.toPublishedAtMillis(): Long? = this
    ?.let { runCatching { Instant.parse(it) }.getOrNull() }
    ?.takeIf { it.isAfter(MIN_PLAUSIBLE_DATE) }
    ?.toEpochMilli()

fun ArticleDto.toEntity(): ArticleEntity = ArticleEntity(
    id = id,
    title = title,
    summary = summary.cleanSummary(),
    imageUrl = imageUrl?.takeIf { it.isNotBlank() },
    newsSite = newsSite,
    authors = authors.map { it.name },
    url = url,
    publishedAtMillis = publishedAt.toPublishedAtMillis(),
)

fun ArticleDto.toDomain(isFavorite: Boolean = false): Article = Article(
    id = id,
    title = title,
    summary = summary.cleanSummary(),
    imageUrl = imageUrl?.takeIf { it.isNotBlank() },
    newsSite = newsSite,
    authors = authors.map { it.name },
    url = url,
    publishedAt = publishedAt.toPublishedAtMillis()?.let(Instant::ofEpochMilli),
    isFavorite = isFavorite,
)

fun ArticleWithFavorite.toDomain(): Article = article.toDomain(isFavorite)

fun ArticleEntity.toDomain(isFavorite: Boolean = false): Article = Article(
    id = id,
    title = title,
    summary = summary,
    imageUrl = imageUrl,
    newsSite = newsSite,
    authors = authors,
    url = url,
    publishedAt = publishedAtMillis?.let(Instant::ofEpochMilli),
    isFavorite = isFavorite,
)

fun FavoriteArticleEntity.toDomain(): Article = Article(
    id = id,
    title = title,
    summary = summary,
    imageUrl = imageUrl,
    newsSite = newsSite,
    authors = authors,
    url = url,
    publishedAt = publishedAtMillis?.let(Instant::ofEpochMilli),
    isFavorite = true,
)

fun Article.toFavoriteEntity(savedAtMillis: Long): FavoriteArticleEntity = FavoriteArticleEntity(
    id = id,
    title = title,
    summary = summary,
    imageUrl = imageUrl,
    newsSite = newsSite,
    authors = authors,
    url = url,
    publishedAtMillis = publishedAt?.toEpochMilli(),
    savedAtMillis = savedAtMillis,
)
