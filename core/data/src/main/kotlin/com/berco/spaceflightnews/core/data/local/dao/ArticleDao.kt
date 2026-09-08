package com.berco.spaceflightnews.core.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.berco.spaceflightnews.core.data.local.entity.ArticleEntity

@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles ORDER BY publishedAtMillis DESC, id DESC")
    fun pagingSource(): PagingSource<Int, ArticleEntity>

    @Query("SELECT * FROM articles WHERE id = :id")
    suspend fun getById(id: Long): ArticleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(articles: List<ArticleEntity>)

    @Query("DELETE FROM articles")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM articles")
    suspend fun count(): Int
}
