package com.berco.spaceflightnews.core.model

/**
 * How the app describes failure, independent of how it was produced. Lives in
 * domain so no layer above data needs to know Retrofit or OkHttp types exist.
 */
sealed interface AppError {
    data object Network : AppError
    data object RateLimited : AppError
    data class Http(val code: Int) : AppError
    data object Serialization : AppError
    data class Unknown(val cause: Throwable) : AppError
}

/**
 * Carries an [AppError] through Throwable-typed channels. Paging's
 * `MediatorResult.Error` and `LoadState.Error` only accept a Throwable, so the
 * error has to travel wrapped.
 */
class AppException(val error: AppError) : Exception()
