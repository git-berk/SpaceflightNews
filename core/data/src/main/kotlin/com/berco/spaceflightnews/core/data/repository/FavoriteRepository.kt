package com.berco.spaceflightnews.core.data.repository

import com.berco.spaceflightnews.core.model.Article
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {

    fun observeFavorites(): Flow<List<Article>>

    fun observeFavoriteIds(): Flow<Set<Long>>

    suspend fun toggle(article: Article)

}
