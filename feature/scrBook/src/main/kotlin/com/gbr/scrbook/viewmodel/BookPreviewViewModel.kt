package com.gbr.scrbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbr.data.repository.TextsRepository
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

    fun loadBook(gitabaseId: GitabaseID, bookId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // Get all books from the repository
                val result = textsRepository.getAllBooks(gitabaseId)

                if (result.isSuccess) {
                    val books = result.getOrThrow()
                    val book = books.find { it.id == bookId }

                    if (book != null) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            book = book,
                            error = null
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            book = null,
                            error = "Book with ID $bookId not found"
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        book = null,
                        error = result.exceptionOrNull()?.message ?: "Failed to load books"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    book = null,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
}

data class BookPreviewUiState(
    val isLoading: Boolean = false,
    val book: BookPreview? = null,
    val error: String? = null
)
