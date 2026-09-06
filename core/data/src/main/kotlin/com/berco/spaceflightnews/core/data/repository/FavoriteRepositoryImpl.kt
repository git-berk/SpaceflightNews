package com.berco.spaceflightnews.core.data.repository

import com.berco.spaceflightnews.core.data.local.dao.FavoriteDao
import com.berco.spaceflightnews.core.data.mapper.toDomain
import com.berco.spaceflightnews.core.data.mapper.toFavoriteEntity
import com.berco.spaceflightnews.core.model.Article
import com.berco.spaceflightnews.core.data.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Clock
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val clock: Clock,
) : FavoriteRepository {

    override fun observeFavorites(): Flow<List<Article>> =
        favoriteDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeFavoriteIds(): Flow<Set<Long>> =
        favoriteDao.observeIds().map { it.toSet() }

    override suspend fun toggle(article: Article) {
        if (favoriteDao.isFavorite(article.id)) {
            favoriteDao.delete(article.id)
        } else {
            // Stores the whole article: the feed cache it came from is cleared
            // on every refresh, but the favourite has to survive that.
            favoriteDao.insert(article.toFavoriteEntity(savedAtMillis = clock.millis()))
        }
    }

    override suspend fun isFavorite(id: Long): Boolean = favoriteDao.isFavorite(id)
}
