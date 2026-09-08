package com.berco.spaceflightnews.core.model

import kotlin.time.Instant

data class Article(
    val id: Long,
    val title: String,
    val summary: String,
    val imageUrl: String?,
    val newsSite: String,
    val authors: List<String>,
    val url: String,
    val publishedAt: Instant?,
    val isFavorite: Boolean = false,
)
