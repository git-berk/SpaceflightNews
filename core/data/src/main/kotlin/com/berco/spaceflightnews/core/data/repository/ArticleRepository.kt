package com.berco.spaceflightnews.core.data.repository

import androidx.paging.PagingData
import com.berco.spaceflightnews.core.model.Article
import kotlinx.coroutines.flow.Flow

interface ArticleRepository {

    fun feed(): Flow<PagingData<Article>>

    /** Network-only; unavailable offline. */
    fun search(query: String): Flow<PagingData<Article>>

    suspend fun getArticle(id: Long): Article?
}
