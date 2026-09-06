package com.berco.spaceflightnews.core.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticleDto(
    val id: Long,
    val title: String,
    val url: String,
    @SerialName("news_site") val newsSite: String,
    @SerialName("published_at") val publishedAt: String,
    // Older records in the feed carry empty summaries and no authors.
    val summary: String = "",
    val authors: List<AuthorDto> = emptyList(),
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    val featured: Boolean = false,
)

@Serializable
data class AuthorDto(
    val name: String,
)
