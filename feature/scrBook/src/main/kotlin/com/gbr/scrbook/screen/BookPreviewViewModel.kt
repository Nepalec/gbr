package com.gbr.scrbook.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbr.data.repository.TextsRepository
import com.gbr.data.repository.UserDataRepository
import com.gbr.model.book.BookContentsTabOptions
import com.gbr.model.book.BookDetail
import com.gbr.model.book.BookImagesTabOptions
import com.gbr.model.book.BookPreview
import com.gbr.model.gitabase.GitabaseID
import com.gbr.model.gitabase.ImageType
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

    // Tab options:
    private val _contentsTabOptions = MutableStateFlow(BookContentsTabOptions())
    val contentsTabOptions: StateFlow<BookContentsTabOptions> = _contentsTabOptions.asStateFlow()

    // Map to store options for each image tab
    private val _imagesTabOptionsMap = MutableStateFlow<Map<ImageType, BookImagesTabOptions>>(emptyMap())
    val imagesTabOptionsMap: StateFlow<Map<ImageType, BookImagesTabOptions>> = _imagesTabOptionsMap.asStateFlow()

    private val _uiState = MutableStateFlow(BookPreviewUiState())
    val uiState: StateFlow<BookPreviewUiState> = _uiState.asStateFlow()

    init {
        // Load contents tab options on ViewModel initialization
        // Image tab options are loaded on-demand when tabs are available
        viewModelScope.launch {
            _contentsTabOptions.value = userDataRepository.getBookContentsTabOptions()
        }
    }

    /**
     * Gets the tab options for a specific ImageType.
     * Loads from preferences if not already in memory.
     */
    suspend fun getImageTabOptions(imageType: ImageType): BookImagesTabOptions {
        val currentMap = _imagesTabOptionsMap.value
        return currentMap[imageType] ?: run {
            val options = userDataRepository.getBookImagesTabOptions(imageType)
            _imagesTabOptionsMap.value = currentMap + (imageType to options)
            options
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

    fun setContentsTabOptions(options: BookContentsTabOptions) {
        viewModelScope.launch {
            _contentsTabOptions.value = options
            userDataRepository.setBookContentsTabOptions(options)
        }
    }

    fun setImageTabOptions(imageType: ImageType, options: BookImagesTabOptions) {
        viewModelScope.launch {
            _imagesTabOptionsMap.value = _imagesTabOptionsMap.value + (imageType to options)
            userDataRepository.setBookImagesTabOptions(imageType, options)
        }
    }
}

data class BookPreviewUiState(
    val isLoading: Boolean = false,
    val bookDetail: BookDetail? = null,
    val error: String? = null
)
