package com.berco.spaceflightnews.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A full snapshot, not a foreign key into [ArticleEntity]. The feed cache is
 * truncated on every refresh; a favourite must outlive it.
 */
@Entity(tableName = "favorites")
data class FavoriteArticleEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val summary: String,
    val imageUrl: String?,
    val newsSite: String,
    val authors: List<String>,
    val url: String,
    val publishedAtMillis: Long?,
    val savedAtMillis: Long,
)
