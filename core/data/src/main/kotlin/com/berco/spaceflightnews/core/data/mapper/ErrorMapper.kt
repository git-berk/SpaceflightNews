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
    is HttpException -> AppError.Http(code())
    is SerializationException -> AppError.Serialization
    else -> AppError.Unknown(this)
}

fun Throwable.asAppException(): AppException = AppException(toAppError())
