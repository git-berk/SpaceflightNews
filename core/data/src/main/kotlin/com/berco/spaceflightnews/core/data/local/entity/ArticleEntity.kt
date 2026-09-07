package com.berco.spaceflightnews.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Disposable feed cache. Cleared on every successful REFRESH. */
@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val summary: String,
    val imageUrl: String?,
    val newsSite: String,
    val authors: List<String>,
    val url: String,
    /** Null when the API sends an implausible date (the 1970 epoch stubs). */
    val publishedAtMillis: Long?,
)
