package com.berco.spaceflightnews.core.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.berco.spaceflightnews.core.data.local.entity.ArticleEntity
import com.berco.spaceflightnews.core.data.local.entity.ArticleWithFavorite

@Dao
interface ArticleDao {

    /**
     * Joining favourites here means Room invalidates this PagingSource when
     * either table changes, so toggling a favourite updates the card with no
     * manual wiring. Null publish dates sort last under DESC in SQLite.
     */
    @Query(
        """
        SELECT a.*, (f.id IS NOT NULL) AS isFavorite
        FROM articles a
        LEFT JOIN favorites f ON a.id = f.id
        ORDER BY a.publishedAtMillis DESC, a.id DESC
        """,
    )
    fun pagingSource(): PagingSource<Int, ArticleWithFavorite>

    @Query(
        """
        SELECT a.*, (f.id IS NOT NULL) AS isFavorite
        FROM articles a
        LEFT JOIN favorites f ON a.id = f.id
        WHERE a.id = :id
        """,
    )
    suspend fun getById(id: Long): ArticleWithFavorite?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(articles: List<ArticleEntity>)

    @Query("DELETE FROM articles")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM articles")
    suspend fun count(): Int
}
