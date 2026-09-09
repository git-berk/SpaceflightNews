package com.berco.spaceflightnews.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.berco.spaceflightnews.MainDispatcherRule
import com.berco.spaceflightnews.core.model.Article
import com.berco.spaceflightnews.fake.FakeArticleRepository
import com.berco.spaceflightnews.fake.FakeFavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

private const val PAST_DEBOUNCE = 1_200L

class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val articles = FakeArticleRepository()
    private val favorites = FakeFavoriteRepository()

    private fun viewModel() = SearchViewModel(articles, favorites, SavedStateHandle())

    /** results is cold; nothing reaches the repository until it is collected. */
    private fun TestScope.collecting(flow: Flow<PagingData<Article>>) {
        backgroundScope.launch { flow.collect {} }
    }

    @Test
    fun `rapid typing issues a single search`() = runTest(mainDispatcherRule.testDispatcher) {
        val vm = viewModel()
        collecting(vm.results)

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
            collecting(vm.results)

            vm.onQueryChange("s")
            advanceTimeBy(PAST_DEBOUNCE)

            assertTrue(articles.searchQueries.isEmpty())
        }

    @Test
    fun `surrounding whitespace is trimmed before searching`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val vm = viewModel()
            collecting(vm.results)

            vm.onQueryChange("  starship  ")
            advanceTimeBy(PAST_DEBOUNCE)

            assertEquals(listOf("starship"), articles.searchQueries)
        }

    @Test
    fun `retyping the same query does not search twice`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val vm = viewModel()
            collecting(vm.results)

            vm.onQueryChange("starship")
            advanceTimeBy(PAST_DEBOUNCE)
            vm.onQueryChange("starship ")
            advanceTimeBy(PAST_DEBOUNCE)

            assertEquals(listOf("starship"), articles.searchQueries)
        }

    @Test
    fun `a searchable query reports work before the debounce elapses`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val vm = viewModel()
            val emissions = mutableListOf<PagingData<Article>>()
            backgroundScope.launch { vm.results.collect { emissions += it } }
            runCurrent()
            val before = emissions.size

            vm.onQueryChange("starship")
            advanceTimeBy(100)

            // Without a new emission the screen keeps the previous empty page,
            // which renders as "no results" until the search actually starts.
            assertEquals(before + 1, emissions.size)
            assertTrue(articles.searchQueries.isEmpty())
        }

    @Test
    fun `the query survives process death`() = runTest(mainDispatcherRule.testDispatcher) {
        val restored = SearchViewModel(
            articles,
            favorites,
            SavedStateHandle(mapOf("search.query" to "voyager")),
        )

        assertEquals("voyager", restored.query.value)
    }

    @Test
    fun `toggling a favourite delegates to the repository`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val vm = viewModel()
            val article = FakeArticleRepository.article(1)

            vm.onToggleFavorite(article)
            runCurrent()

            assertEquals(listOf(article), favorites.toggled)
        }
}
