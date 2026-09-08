package com.berco.spaceflightnews.ui.detail

import androidx.lifecycle.SavedStateHandle
import com.berco.spaceflightnews.MainDispatcherRule
import com.berco.spaceflightnews.fake.FakeArticleRepository
import com.berco.spaceflightnews.fake.FakeFavoriteRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private const val ARTICLE_ID = 1L
private const val MISSING_ID = 999L

// toRoute() decodes through Bundle, which is stubbed in a plain JVM test and
// silently yields 0 for every argument.
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ArticleDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val articles = FakeArticleRepository()
    private val favorites = FakeFavoriteRepository()

    private fun viewModel(id: Long = ARTICLE_ID) = ArticleDetailViewModel(
        articles,
        favorites,
        SavedStateHandle(mapOf("articleId" to id)),
    )

    private fun content(state: ArticleDetailUiState) = state as ArticleDetailUiState.Content

    @Test
    fun `a cached article becomes content`() = runTest(mainDispatcherRule.testDispatcher) {
        val vm = viewModel()
        runCurrent()

        assertEquals(ARTICLE_ID, content(vm.uiState.value).article.id)
    }

    @Test
    fun `a missing article resolves to not found rather than loading forever`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val vm = viewModel(MISSING_ID)
            runCurrent()

            // The old shape used null for both, so this case span the spinner forever.
            assertEquals(ArticleDetailUiState.NotFound, vm.uiState.value)
        }

    @Test
    fun `the article is read once even after the screen resubscribes`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val vm = viewModel()
            var job: Job = backgroundScope.launch { vm.uiState.collect {} }
            runCurrent()
            job.cancel()

            // Longer than any WhileSubscribed timeout the screen might use.
            advanceTimeBy(10_000)
            job = backgroundScope.launch { vm.uiState.collect {} }
            runCurrent()

            assertEquals(1, articles.getArticleCalls)
        }

    @Test
    fun `favouriting updates the article already on screen`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val vm = viewModel()
            runCurrent()
            assertFalse(content(vm.uiState.value).article.isFavorite)

            vm.onToggleFavorite(content(vm.uiState.value).article)
            runCurrent()

            assertTrue(content(vm.uiState.value).article.isFavorite)
            assertEquals(1, favorites.toggled.size)
        }

    @Test
    fun `favourites observed while the article loads survive the load`() =
        runTest(mainDispatcherRule.testDispatcher) {
            favorites.toggle(FakeArticleRepository.article(ARTICLE_ID))
            // The repository fake reports isFavorite = false, so only the observed
            // ids can make this true — which is the invariant under test.
            articles.getArticleDelayMillis = 100

            val vm = viewModel()
            advanceTimeBy(200)
            runCurrent()

            assertTrue(content(vm.uiState.value).article.isFavorite)
        }

    @Test
    fun `a favourite emission cannot resurrect a missing article`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val vm = viewModel(MISSING_ID)
            runCurrent()

            favorites.toggle(FakeArticleRepository.article(MISSING_ID))
            runCurrent()

            assertEquals(ArticleDetailUiState.NotFound, vm.uiState.value)
        }
}
