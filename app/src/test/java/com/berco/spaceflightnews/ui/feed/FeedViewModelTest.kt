package com.berco.spaceflightnews.ui.feed

import androidx.paging.testing.asSnapshot
import com.berco.spaceflightnews.MainDispatcherRule
import com.berco.spaceflightnews.fake.FakeArticleRepository
import com.berco.spaceflightnews.fake.FakeFavoriteRepository
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class FeedViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val articles = FakeArticleRepository()
    private val favorites = FakeFavoriteRepository()

    private fun viewModel() = FeedViewModel(articles, favorites)

    @Test
    fun `the feed reports which articles are favourites`() =
        runTest(mainDispatcherRule.testDispatcher) {
            favorites.toggle(FakeArticleRepository.article(2))
            val vm = viewModel()

            val flags = vm.feed.asSnapshot().associate { it.id to it.isFavorite }

            assertEquals(mapOf(1L to false, 2L to true), flags)
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
