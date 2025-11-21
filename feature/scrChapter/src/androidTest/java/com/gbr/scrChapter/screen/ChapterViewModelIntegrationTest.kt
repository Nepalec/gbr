package com.gbr.scrChapter.screen

import app.cash.turbine.test
import com.gbr.data.test.BaseViewModelTest
import com.gbr.model.book.BookPreview
import com.gbr.model.gitabase.GitabaseID
import com.gbr.scrchapter.screen.ChapterViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Integration test for ChapterViewModel.
 * Tests the full flow: ViewModel -> UseCase -> Repository -> Database
 * Uses real test databases from assets.
 */
class ChapterViewModelIntegrationTest : BaseViewModelTest() {
    lateinit var viewModel: ChapterViewModel
    lateinit var bookPreview: BookPreview
    val chaptersGitabase = GitabaseID("folio_eng")

    @Before
    fun setup() = runTest {
        initializeGitabases()
        // Get real BookPreview from repository
        val allBooksResult = textsRepository.getAllBooks(chaptersGitabase)
        assertTrue("Should load books from test DB", allBooksResult.isSuccess)

        val books = allBooksResult.getOrThrow()
        assertTrue("Should have at least one book", books.isNotEmpty())
        bookPreview = books.first()

        // Create ViewModel with real UseCase and mock UserPreferencesRepository
        viewModel = ChapterViewModel(
            textsRepository,
            fixture.stringProvider
        )
    }

    @Test
    fun loadChapter_loadsBookDetailSuccessfullyFromTestDatabase() = runTest {

        viewModel.loadChapter(testGitabaseId, bookPreview, 1)

        val uiState = viewModel.uiState.first { state ->
            !state.isLoading || state.error != null
        }

        assertNotNull("BookDetail should be loaded", uiState.bookDetail)
        assertEquals("Book should match", bookPreview.id, uiState.bookDetail?.book?.id)
        assertFalse("Should not be loading", uiState.isLoading)
        assertNull("Should not have error", uiState.error)

        val bookDetail = uiState.bookDetail!!
        assertNotNull("BookDetail should have book", bookDetail.book)
        assertEquals("Book ID should match", bookPreview.id, bookDetail.book.id)
    }

    @Test
    fun loadChapter_handlesLoadingStateCorrectly() = runTest {
        viewModel.uiState.test {
            viewModel.loadChapter(testGitabaseId, bookPreview, 1)

            val initial = awaitItem()
            assertTrue("Should be loading initially", initial.isLoading)

            val final = awaitItem()
            assertFalse("Should not be loading after completion", final.isLoading)
            assertTrue(
                "Should have bookDetail or error",
                final.bookDetail != null || final.error != null
            )
            cancelAndIgnoreRemainingEvents()
        }
    }
}

