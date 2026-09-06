package com.berco.spaceflightnews.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.berco.spaceflightnews.core.data.local.converter.Converters
import com.berco.spaceflightnews.core.data.local.dao.ArticleDao
import com.berco.spaceflightnews.core.data.local.dao.FavoriteDao
import com.berco.spaceflightnews.core.data.local.dao.RemoteKeyDao
import com.berco.spaceflightnews.core.data.local.entity.ArticleEntity
import com.berco.spaceflightnews.core.data.local.entity.FavoriteArticleEntity
import com.berco.spaceflightnews.core.data.local.entity.RemoteKeyEntity

@Database(
    entities = [ArticleEntity::class, FavoriteArticleEntity::class, RemoteKeyEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class SpaceflightDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun remoteKeyDao(): RemoteKeyDao

    companion object {
        const val NAME = "spaceflight.db"
    }
}
