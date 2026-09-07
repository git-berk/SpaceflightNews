package com.berco.spaceflightnews.core.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.berco.spaceflightnews.core.data.local.entity.ArticleEntity

@Dao
interface ArticleDao {

    /**
     * Deliberately does not join favourites. Room observes every table a query
     * touches, so joining would invalidate this PagingSource on each favourite
     * toggle and force a full refresh, collapsing the loaded window and
     * shifting the list under the reader. Favourite state is layered on above
     * the pager instead. Null publish dates sort last under DESC in SQLite.
     */
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
