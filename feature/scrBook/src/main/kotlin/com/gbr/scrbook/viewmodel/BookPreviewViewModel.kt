package com.gbr.scrbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbr.data.repository.TextsRepository
import com.gbr.model.book.BookDetail
import com.gbr.model.book.BookPreview
import com.gbr.model.gitabase.GitabaseID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookPreviewViewModel @Inject constructor(
    private val textsRepository: TextsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookPreviewUiState())
    val uiState: StateFlow<BookPreviewUiState> = _uiState.asStateFlow()

    fun loadBook(gitabaseId: GitabaseID, bookPreview: BookPreview) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // Get book detail using the new composition approach
                val result = textsRepository.getBookDetail(gitabaseId, bookPreview)

                if (result.isSuccess) {
                    val bookDetail = result.getOrThrow()

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        bookDetail = bookDetail,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        bookDetail = null,
                        error = result.exceptionOrNull()?.message ?: "Failed to load book detail"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    bookDetail = null,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
}

data class BookPreviewUiState(
    val isLoading: Boolean = false,
    val bookDetail: BookDetail? = null,
    val error: String? = null
)
