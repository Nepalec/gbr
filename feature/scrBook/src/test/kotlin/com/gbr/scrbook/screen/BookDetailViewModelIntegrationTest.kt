package com.gbr.scrbook.screen

import app.cash.turbine.test
import com.gbr.data.repository.UserPreferencesRepository
import com.gbr.data.test.BaseViewModelTest
import com.gbr.data.test.MainCoroutineRule
import com.gbr.model.book.BookContentsTabOptions
import com.gbr.model.book.BookPreview
import com.gbr.model.gitabase.ImageType
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Integration test for BookDetailViewModel using Robolectric.
 * Tests the full flow: ViewModel -> UseCase -> Repository -> Database
 * Uses real test databases from assets.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], manifest = Config.NONE)
class BookDetailViewModelIntegrationTest : BaseViewModelTest() {
    @get:Rule
    val mainRule = MainCoroutineRule()

    lateinit var viewModel: BookDetailViewModel
    lateinit var bookPreview: BookPreview
    lateinit var userPreferencesRepository: UserPreferencesRepository

    @Before
    fun setup() = runTest {
        initializeGitabases()

        // Get real BookPreview from repository
        val allBooksResult = textsRepository.getAllBooks(testGitabaseId)
        assertTrue("Should load books from test DB", allBooksResult.isSuccess)

        val books = allBooksResult.getOrThrow()
        assertTrue("Should have at least one book", books.isNotEmpty())
        bookPreview = books.first()

        // Create mock UserPreferencesRepository
        userPreferencesRepository = mockk(relaxed = true) {
            coEvery { bookContentsTabOptions } returns flowOf(
                com.gbr.model.book.BookContentsTabOptions()
            )
        }
    }

    @Test
    fun loadBook_loadsBookDetailSuccessfullyFromTestDatabase() = runTest {

        viewModel = BookDetailViewModel(
            userPreferencesRepository = userPreferencesRepository,
            loadBookDetailUseCase = loadBookDetailUseCase
        )

        viewModel.loadBook(testGitabaseId, bookPreview)

        val uiState = viewModel.uiState.first { state ->
            !state.isLoading && (state.bookDetail != null || state.error != null)
        }

        assertNotNull("BookDetail should be loaded", uiState.bookDetail)
        assertEquals("Book should match", bookPreview.id, uiState.bookDetail?.book?.id)
        assertFalse("Should not be loading", uiState.isLoading)
        assertNull("Should not have error", uiState.error)

        val bookDetail = uiState.bookDetail!!
        assertNotNull("BookDetail should have book", bookDetail.book)
        assertEquals("gitabase.com", bookDetail.book.author)
        assertEquals(ImageType.PICTURE, bookDetail.imageTabs?.get(0)?.type)
        assertEquals(15, bookDetail.texts?.size)
        assertEquals("Book ID should match", bookPreview.id, bookDetail.book.id)
    }

    @Test
    fun loadBook_handlesLoadingStateCorrectly() = runTest {
        viewModel = BookDetailViewModel(
            userPreferencesRepository = userPreferencesRepository,
            loadBookDetailUseCase = loadBookDetailUseCase
        )
        viewModel.uiState.test {
            viewModel.loadBook(testGitabaseId, bookPreview)

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

    @Test
    fun loadBook_loadsContentsTabOptionsOnInitialization() = runTest {
        // Arrange
        val expectedOptions = BookContentsTabOptions(
            textSize = 1,
            columns = 3
        )

        val userPreferencesRepository: UserPreferencesRepository = mockk(relaxed = true) {
            coEvery { bookContentsTabOptions } returns flowOf(expectedOptions)
        }

        // Act
        val viewModel = BookDetailViewModel(
            userPreferencesRepository = userPreferencesRepository,
            loadBookDetailUseCase = loadBookDetailUseCase
        )

        viewModel.contentsTabOptions.test {
            val options = awaitItem()
            assertEquals("Text size should match", expectedOptions.textSize, options.textSize)
            assertEquals("Columns should match", expectedOptions.columns, options.columns)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

