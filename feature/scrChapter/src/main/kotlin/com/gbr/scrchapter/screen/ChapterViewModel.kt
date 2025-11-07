package com.gbr.scrchapter.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbr.data.repository.TextsRepository
import com.gbr.model.book.BookDetail
import com.gbr.model.book.BookPreview
import com.gbr.model.book.ChapterContentsItem
import com.gbr.model.book.TextItem
import com.gbr.model.gitabase.GitabaseID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChapterViewModel @Inject constructor(
    private val textsRepository: TextsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChapterUiState())
    val uiState: StateFlow<ChapterUiState> = _uiState.asStateFlow()

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
                                error = if (chapter == null) "Chapter not found" else null
                            )
                        }.onFailure { error ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = error.message ?: "Failed to load book detail"
                            )
                        }
                    }
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load book preview"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }
}

data class ChapterUiState(
    val isLoading: Boolean = false,
    val bookDetail: BookDetail? = null,
    val chapter: ChapterContentsItem? = null,
    val chapterTexts: List<TextItem> = emptyList(),
    val error: String? = null
)

