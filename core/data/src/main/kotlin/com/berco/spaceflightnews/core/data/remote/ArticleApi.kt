package com.berco.spaceflightnews.core.data.remote

import com.berco.spaceflightnews.core.data.remote.dto.ArticleDto
import com.berco.spaceflightnews.core.data.remote.dto.PagedResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ArticleApi {

    /**
     * [publishedAtLte] pins a snapshot for the duration of one pagination session.
     * Without it, articles published mid-scroll shift the offset window and pages
     * re-serve rows the previous page already returned.
     */
    @GET("articles/")
    suspend fun getArticles(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("published_at_lte") publishedAtLte: String? = null,
        @Query("ordering") ordering: String = "-published_at",
    ): PagedResponseDto<ArticleDto>

    @GET("articles/")
    suspend fun searchArticles(
        @Query("search") query: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("published_at_lte") publishedAtLte: String? = null,
        @Query("ordering") ordering: String = "-published_at",
    ): PagedResponseDto<ArticleDto>

    @GET("articles/{id}/")
    suspend fun getArticle(@Path("id") id: Long): ArticleDto
}
