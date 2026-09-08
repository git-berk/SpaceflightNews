package com.berco.spaceflightnews.ui.feed

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import androidx.paging.testing.asSnapshot
import com.berco.spaceflightnews.MainDispatcherRule
import com.berco.spaceflightnews.core.model.Article
import com.berco.spaceflightnews.fake.FakeArticleRepository
import com.berco.spaceflightnews.fake.FakeFavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

private const val PAST_DEBOUNCE = 600L

class FeedViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val articles = FakeArticleRepository()
    private val favorites = FakeFavoriteRepository()

    private fun viewModel(savedState: SavedStateHandle = SavedStateHandle()) =
        FeedViewModel(articles, favorites, savedState)

    /** searchResults is cold; nothing reaches the repository until it is collected. */
    private fun kotlinx.coroutines.test.TestScope.collecting(flow: Flow<PagingData<Article>>) {
        backgroundScope.launch { flow.collect {} }
    }

    @Test
    fun `the query is exposed through ui state`() = runTest(mainDispatcherRule.testDispatcher) {
        val vm = viewModel()
        backgroundScope.launch { vm.uiState.collect {} }

        vm.onQueryChange("starship")

        assertEquals("starship", vm.uiState.value.query)
    }

    @Test
    fun `closing search clears the query`() = runTest(mainDispatcherRule.testDispatcher) {
        val vm = viewModel()
        backgroundScope.launch { vm.uiState.collect {} }

        vm.onSearchActiveChange(true)
        vm.onQueryChange("starship")
        vm.onSearchActiveChange(false)

        assertEquals("", vm.uiState.value.query)
        assertFalse(vm.uiState.value.isSearchActive)
    }

    @Test
    fun `a restored query survives process death`() = runTest(mainDispatcherRule.testDispatcher) {
        val restored = SavedStateHandle(mapOf("feed.query" to "voyager"))
        val vm = viewModel(restored)
        backgroundScope.launch { vm.uiState.collect {} }

        assertEquals("voyager", vm.uiState.value.query)
    }

    @Test
    fun `rapid typing issues a single search`() = runTest(mainDispatcherRule.testDispatcher) {
        val vm = viewModel()
        collecting(vm.searchResults)

        vm.onQueryChange("s")
        advanceTimeBy(100)
        vm.onQueryChange("st")
        advanceTimeBy(100)
        vm.onQueryChange("starship")
        advanceTimeBy(PAST_DEBOUNCE)

        assertEquals(listOf("starship"), articles.searchQueries)
    }

    @Test
    fun `a query shorter than the minimum never reaches the repository`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val vm = viewModel()
            collecting(vm.searchResults)

            vm.onQueryChange("s")
            advanceTimeBy(PAST_DEBOUNCE)

            assertTrue(articles.searchQueries.isEmpty())
        }

    @Test
    fun `surrounding whitespace is trimmed before searching`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val vm = viewModel()
            collecting(vm.searchResults)

            vm.onQueryChange("  starship  ")
            advanceTimeBy(PAST_DEBOUNCE)

            assertEquals(listOf("starship"), articles.searchQueries)
        }

    @Test
    fun `retyping the same query does not search twice`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val vm = viewModel()
            collecting(vm.searchResults)

            vm.onQueryChange("starship")
            advanceTimeBy(PAST_DEBOUNCE)
            vm.onQueryChange("starship ")
            advanceTimeBy(PAST_DEBOUNCE)

            assertEquals(listOf("starship"), articles.searchQueries)
        }

    @Test
    fun `clearing the query back below the minimum issues no further search`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val vm = viewModel()
            collecting(vm.searchResults)

            vm.onQueryChange("starship")
            advanceTimeBy(PAST_DEBOUNCE)
            vm.onQueryChange("")
            advanceTimeBy(PAST_DEBOUNCE)

            assertEquals(listOf("starship"), articles.searchQueries)
        }

    @Test
    fun `toggling a favourite delegates to the repository`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val vm = viewModel()
            val article = FakeArticleRepository.article(1)

            vm.onToggleFavorite(article)
            advanceTimeBy(1)

            assertEquals(listOf(article), favorites.toggled)
        }

    @Test
    fun `the feed reports which articles are favourites`() =
        runTest(mainDispatcherRule.testDispatcher) {
            favorites.toggle(FakeArticleRepository.article(2))
            val vm = viewModel()

            val flags = vm.feed.asSnapshot().associate { it.id to it.isFavorite }

            assertEquals(mapOf(1L to false, 2L to true), flags)
        }
}
