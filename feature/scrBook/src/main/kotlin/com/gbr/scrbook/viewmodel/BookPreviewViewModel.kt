package com.gbr.scrbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbr.data.repository.TextsRepository
import com.gbr.data.repository.UserDataRepository
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
    private val textsRepository: TextsRepository,
    private val userDataRepository: UserDataRepository
) : ViewModel() {
    
    private val _textSize = MutableStateFlow(0)
    val textSize: StateFlow<Int> = _textSize.asStateFlow()
    
    private val _contentsColumns = MutableStateFlow(1)
    val contentsColumns: StateFlow<Int> = _contentsColumns.asStateFlow()
    
    // Map to store columns for each ImageType (key = ImageType.value)
    private val _imagesColumnsMap = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val imagesColumnsMap: StateFlow<Map<Int, Int>> = _imagesColumnsMap.asStateFlow()

    private val _uiState = MutableStateFlow(BookPreviewUiState())
    val uiState: StateFlow<BookPreviewUiState> = _uiState.asStateFlow()

    init {
        // Load text size and contents columns preferences on ViewModel initialization
        // Image tab columns are loaded on-demand when tabs are available
        viewModelScope.launch {
            _textSize.value = userDataRepository.getBookContentsTextSize()
            _contentsColumns.value = userDataRepository.getBookContentsColumns()
        }
    }
    
    /**
     * Gets the columns for a specific ImageType.
     * Loads from preferences if not already in memory.
     */
    suspend fun getImageTabColumns(imageTypeValue: Int): Int {
        val currentMap = _imagesColumnsMap.value
        return currentMap[imageTypeValue] ?: run {
            val columns = userDataRepository.getBookImagesColumns(imageTypeValue)
            _imagesColumnsMap.value = currentMap + (imageTypeValue to columns)
            columns
        }
    }
    
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
    
    fun setTextSize(textSize: Int) {
        viewModelScope.launch {
            _textSize.value = textSize
            userDataRepository.setBookContentsTextSize(textSize)
        }
    }
    
    fun setContentsColumns(columns: Int) {
        viewModelScope.launch {
            _contentsColumns.value = columns
            userDataRepository.setBookContentsColumns(columns)
        }
    }
    
    fun setImagesColumns(imageTypeValue: Int, columns: Int) {
        viewModelScope.launch {
            _imagesColumnsMap.value = _imagesColumnsMap.value + (imageTypeValue to columns)
            userDataRepository.setBookImagesColumns(imageTypeValue, columns)
        }
    }
}

data class BookPreviewUiState(
    val isLoading: Boolean = false,
    val bookDetail: BookDetail? = null,
    val error: String? = null
)
