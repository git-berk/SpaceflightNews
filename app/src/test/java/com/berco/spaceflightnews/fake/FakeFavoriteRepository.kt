package com.berco.spaceflightnews.fake

import com.berco.spaceflightnews.core.data.repository.FavoriteRepository
import com.berco.spaceflightnews.core.model.Article
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeFavoriteRepository : FavoriteRepository {

    private val favorites = MutableStateFlow<Map<Long, Article>>(emptyMap())

    val toggled = mutableListOf<Article>()

    override fun observeFavorites(): Flow<List<Article>> = favorites.map { it.values.toList() }

    override fun observeFavoriteIds(): Flow<Set<Long>> = favorites.map { it.keys }

    override suspend fun toggle(article: Article) {
        toggled += article
        favorites.value = favorites.value.toMutableMap().apply {
            if (remove(article.id) == null) put(article.id, article)
        }
    }

}
