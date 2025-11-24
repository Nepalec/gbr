package com.gbr.scrChapter.screen

import com.gbr.data.test.BaseViewModelTest
import com.gbr.data.test.MainCoroutineRule
import com.gbr.model.book.BookPreview
import com.gbr.model.gitabase.GitabaseID
import com.gbr.scrchapter.screen.ChapterViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], manifest = Config.NONE)
@OptIn(ExperimentalCoroutinesApi::class)
class ChapterViewModelIntegrationTest : BaseViewModelTest() {

    @get:Rule
    val mainRule = MainCoroutineRule()

    lateinit var bookPreview: BookPreview

    val chaptersGitabase = GitabaseID("folio_eng")
    val chapterNumber = 1

    @Before
    fun setup() = runTest {
        initializeGitabases()
        bookPreview = textsRepository.getAllBooks(chaptersGitabase).getOrThrow().first()
    }

    @Test
    fun loadChapter_loadsBookDetailSuccessfullyFromTestDatabase() = runTest {
        val viewModel = ChapterViewModel(
            textsRepository,
            fixture.stringProvider
        )

        viewModel.loadChapter(chaptersGitabase, bookPreview, chapterNumber)

        val uiState = viewModel.uiState.first { state ->
            !state.isLoading || state.error != null
        }

        assertNotNull(uiState.bookDetail)
        assertEquals(chapterNumber, uiState.chapter?.number)
        assertFalse(uiState.isLoading)
        assertNull(uiState.error)
        assertEquals(bookPreview.id, uiState.bookDetail?.book?.id)
    }
}


