package com.gbr.scrChapter.screen

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
    val chapterNumber = 1

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

        viewModel.loadChapter(chaptersGitabase, bookPreview, chapterNumber)

        val uiState = viewModel.uiState.first { state ->
            !state.isLoading || state.error != null
        }

        val bookDetail = uiState.bookDetail
        assertNotNull("BookDetail should be loaded", bookDetail)
        assertEquals("Book ID should be right", uiState.chapter?.book?.id, bookDetail?.book?.id)
        assertEquals("Chapter should match", chapterNumber, uiState.chapter?.number)
        assertFalse("Should not be loading", uiState.isLoading)
        assertNull("Should not have error", uiState.error)
        assertNotNull("BookDetail should have book", bookDetail?.book)
        assertEquals("Book ID should match", bookPreview.id, bookDetail?.book?.id)
    }

}

