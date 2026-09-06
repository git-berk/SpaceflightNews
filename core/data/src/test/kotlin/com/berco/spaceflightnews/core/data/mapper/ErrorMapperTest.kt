package com.berco.spaceflightnews.core.data.mapper

import com.berco.spaceflightnews.core.model.AppError
import com.berco.spaceflightnews.core.model.AppException
import kotlinx.serialization.SerializationException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.UnknownHostException

class ErrorMapperTest {

    @Test
    fun `io failures map to network`() {
        assertEquals(AppError.Network, IOException("offline").toAppError())
        assertEquals(AppError.Network, UnknownHostException("dns").toAppError())
    }

    @Test
    fun `http failures keep their status code`() {
        val notFound = HttpException(
            Response.error<Any>(404, "".toResponseBody("application/json".toMediaType())),
        )

        assertEquals(AppError.Http(404), notFound.toAppError())
    }

    @Test
    fun `serialization failures map to serialization`() {
        assertEquals(AppError.Serialization, SerializationException("bad json").toAppError())
    }

    @Test
    fun `unknown failures carry the cause`() {
        val cause = IllegalStateException("boom")
        val mapped = cause.toAppError()

        assertTrue(mapped is AppError.Unknown)
        assertSame(cause, (mapped as AppError.Unknown).cause)
    }

    @Test
    fun `an already mapped error is not rewrapped`() {
        val wrapped = AppException(AppError.Http(500))

        assertEquals(AppError.Http(500), wrapped.toAppError())
    }
}
