package com.gbr.scrbook.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbr.data.repository.UserDataRepository
import com.gbr.data.usecase.ImageLoadingState
import com.gbr.data.usecase.LoadBookDetailUseCase
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
class BookDetailViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    private val loadBookDetailUseCase: LoadBookDetailUseCase
) : ViewModel() {

    // Tab options:
    private val _contentsTabOptions = MutableStateFlow(BookContentsTabOptions())
    val contentsTabOptions: StateFlow<BookContentsTabOptions> = _contentsTabOptions.asStateFlow()

    // Map to store options for each image tab
    private val _imagesTabOptionsMap = MutableStateFlow<Map<ImageType, BookImagesTabOptions>>(emptyMap())
    val imagesTabOptionsMap: StateFlow<Map<ImageType, BookImagesTabOptions>> = _imagesTabOptionsMap.asStateFlow()

    private val _uiState = MutableStateFlow(BookDetailUiState())
    val uiState: StateFlow<BookDetailUiState> = _uiState.asStateFlow()

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
            loadBookDetailUseCase.execute(gitabaseId, bookPreview).collect { state ->
                // Handle errors first
                if (state.error != null) {
                    _uiState.value = BookDetailUiState(
                        isLoading = false,
                        bookDetail = null,
                        imageFilesExtracted = false,
                        error = state.error
                    )
                    return@collect
                }

                when (state.imageLoadingState) {
                    is ImageLoadingState.LoadingMetadata -> {
                        _uiState.value = BookDetailUiState(
                            isLoading = true,
                            bookDetail = null,
                            imageFilesExtracted = false,
                            error = null
                        )
                    }
                    is ImageLoadingState.MetadataReady -> {
                        _uiState.value = BookDetailUiState(
                            isLoading = false,
                            bookDetail = state.bookDetail,
                            imageFilesExtracted = false,  // Show placeholders
                            error = null
                        )
                    }
                    is ImageLoadingState.Ready -> {
                        _uiState.value = BookDetailUiState(
                            isLoading = false,
                            bookDetail = state.bookDetail,
                            imageFilesExtracted = (state.imageLoadingState as ImageLoadingState.Ready).imageFilesExtracted,
                            error = null
                        )
                    }
                }
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

data class BookDetailUiState(
    val isLoading: Boolean = false,
    val imageFilesExtracted: Boolean = false,
    val bookDetail: BookDetail? = null,
    val error: String? = null
)
