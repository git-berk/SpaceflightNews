package com.berco.spaceflightnews.core.data.di

import android.content.Context
import androidx.room.Room
import com.berco.spaceflightnews.core.data.local.SpaceflightDatabase
import com.berco.spaceflightnews.core.data.local.dao.ArticleDao
import com.berco.spaceflightnews.core.data.local.dao.FavoriteDao
import com.berco.spaceflightnews.core.data.local.dao.RemoteKeyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SpaceflightDatabase =
        Room.databaseBuilder(context, SpaceflightDatabase::class.java, SpaceflightDatabase.NAME)
            .build()

    @Provides
    fun provideArticleDao(db: SpaceflightDatabase): ArticleDao = db.articleDao()

    @Provides
    fun provideFavoriteDao(db: SpaceflightDatabase): FavoriteDao = db.favoriteDao()

    @Provides
    fun provideRemoteKeyDao(db: SpaceflightDatabase): RemoteKeyDao = db.remoteKeyDao()
}
