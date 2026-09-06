package com.berco.spaceflightnews.core.data.paging

import androidx.paging.PagingSource
import com.berco.spaceflightnews.core.data.fake.FakeArticleApi
import com.berco.spaceflightnews.core.model.AppError
import com.berco.spaceflightnews.core.model.AppException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchArticlePagingSourceTest {

    private val api = FakeArticleApi()
    private val source = SearchArticlePagingSource(api, "starship")

    private fun refresh(key: Int? = null) = PagingSource.LoadParams.Refresh(
        key = key,
        loadSize = 20,
        placeholdersEnabled = false,
    )

    @Test
    fun `first load starts at offset zero and reports the next key`() = runTest {
        val result = source.load(refresh()) as PagingSource.LoadResult.Page

        assertEquals(20, result.data.size)
        assertEquals(0, api.calls.single().offset)
        assertNull(result.prevKey)
        assertEquals(20, result.nextKey)
    }

    @Test
    fun `the final page reports no next key`() = runTest {
        api.totalAvailable = 10

        val result = source.load(refresh()) as PagingSource.LoadResult.Page

        assertEquals(10, result.data.size)
        assertNull(result.nextKey)
    }

    @Test
    fun `failures surface as a mapped AppError`() = runTest {
        api.failWith = FakeArticleApi.offline()

        val result = source.load(refresh()) as PagingSource.LoadResult.Error
        val error = result.throwable

        assertTrue(error is AppException)
        assertEquals(AppError.Network, (error as AppException).error)
    }
}
