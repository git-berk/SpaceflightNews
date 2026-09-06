package com.berco.spaceflightnews.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PagedResponseDto<T>(
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    val results: List<T>,
)
