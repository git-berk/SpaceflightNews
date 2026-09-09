package com.berco.spaceflightnews.core.data.mapper

import com.berco.spaceflightnews.core.model.AppError
import com.berco.spaceflightnews.core.model.AppException
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

/**
 * The only place Retrofit/OkHttp types are translated. Everything above the data
 * layer works with [AppError] and never imports a networking class.
 */
fun Throwable.toAppError(): AppError = when (this) {
    is AppException -> error
    is IOException -> AppError.Network
    is HttpException -> if (code() == HTTP_TOO_MANY_REQUESTS) {
        AppError.RateLimited
    } else {
        AppError.Http(code())
    }
    is SerializationException -> AppError.Serialization
    else -> AppError.Unknown(this)
}

fun Throwable.asAppException(): AppException = AppException(toAppError())

/** RFC 6585: the only meaning 429 carries. */
private const val HTTP_TOO_MANY_REQUESTS = 429
