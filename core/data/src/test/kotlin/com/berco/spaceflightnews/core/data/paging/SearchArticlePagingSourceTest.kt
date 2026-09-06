package com.berco.spaceflightnews.core.data.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.berco.spaceflightnews.core.data.fake.FakeArticleApi
import com.berco.spaceflightnews.core.model.AppError
import com.berco.spaceflightnews.core.model.Article
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
    fun `refreshing a search restarts at the first page`() = runTest {
        val state = PagingState<Int, Article>(
            pages = listOf(
                PagingSource.LoadResult.Page(emptyList(), prevKey = null, nextKey = 40),
            ),
            anchorPosition = 25,
            config = PagingConfig(pageSize = 20),
            leadingPlaceholderCount = 0,
        )

        assertNull(source.getRefreshKey(state))
    }

    @Test
    fun `an empty page ends pagination even if the API reports a next link`() = runTest {
        api.totalAvailable = 0
        api.alwaysReportNext = true

        val result = source.load(refresh()) as PagingSource.LoadResult.Page

        assertTrue(result.data.isEmpty())
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
