package com.gbr.scrchapter.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbr.common.strings.StringProvider
import com.gbr.data.repository.TextsRepository
import com.gbr.model.book.BookDetail
import com.gbr.model.book.BookPreview
import com.gbr.model.book.ChapterContentsItem
import com.gbr.model.book.TextItem
import com.gbr.model.gitabase.GitabaseID
import com.gbr.scrchapter.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScrollPosition(
    val firstVisibleItemIndex: Int,
    val firstVisibleItemScrollOffset: Int
)

@HiltViewModel
class ChapterViewModel @Inject constructor(
    private val textsRepository: TextsRepository,
    private val stringProvider: StringProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChapterUiState())
    val uiState: StateFlow<ChapterUiState> = _uiState.asStateFlow()

    private val _scrollPositions = MutableStateFlow<Map<Int, ScrollPosition>>(emptyMap())

    fun saveScrollPosition(chapterNumber: Int, firstVisibleItemIndex: Int, firstVisibleItemScrollOffset: Int) {
        _scrollPositions.value = _scrollPositions.value + (chapterNumber to ScrollPosition(
            firstVisibleItemIndex = firstVisibleItemIndex,
            firstVisibleItemScrollOffset = firstVisibleItemScrollOffset
        ))
    }

    fun getScrollPosition(chapterNumber: Int): ScrollPosition? {
        return _scrollPositions.value[chapterNumber]
    }

    fun loadChapter(gitabaseId: GitabaseID, bookPreview: BookPreview, chapterNumber: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // First get BookPreview (already have it, but ensure we have latest)
                val previewResult = textsRepository.getBookPreviewById(gitabaseId, bookPreview.id)
                previewResult.onSuccess { preview ->
                    preview?.let { book ->
                        // Then get BookDetail with extractImages = false
                        val detailResult = textsRepository.getBookDetail(gitabaseId, book, extractImages = false)
                        detailResult.onSuccess { detail ->
                            // Find the chapter by number
                            val chapter = detail.chapters?.find { it.number == chapterNumber }

                            // Load chapter texts
                            val textsResult = textsRepository.getChapterTexts(gitabaseId, book, chapterNumber)
                            val texts = textsResult.getOrNull() ?: emptyList()

                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                bookDetail = detail,
                                chapter = chapter,
                                chapterTexts = texts,
                                error = if (chapter == null) stringProvider.getString(R.string.error_chapter_not_found) else null
                            )
                        }.onFailure { error ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = error.message
                                    ?: stringProvider.getString(R.string.error_failed_to_load_book_detail)
                            )
                        }
                    }
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: stringProvider.getString(R.string.error_failed_to_load_book_preview)
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: stringProvider.getString(R.string.error_unknown)
                )
            }
        }
    }
}

data class ChapterUiState(
    val isLoading: Boolean = true,
    val bookDetail: BookDetail? = null,
    val chapter: ChapterContentsItem? = null,
    val chapterTexts: List<TextItem> = emptyList(),
    val error: String? = null
)

