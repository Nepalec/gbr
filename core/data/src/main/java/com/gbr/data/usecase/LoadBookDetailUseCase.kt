package com.gbr.data.usecase

import com.gbr.data.repository.ImageFilesRepository
import com.gbr.data.repository.TextsRepository
import com.gbr.model.book.BookDetail
import com.gbr.model.book.BookPreview
import com.gbr.model.gitabase.GitabaseID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for loading book detail with progressive image extraction.
 * Handles 2-stage loading: first gets metadata without bitmaps, then extracts images if needed.
 * Supports parallel loading of images and notes.
 */
class LoadBookDetailUseCase @Inject constructor(
    private val textsRepository: TextsRepository,
    private val imageFilesRepository: ImageFilesRepository
) {
    /**
     * Executes the book detail loading process with progressive state emission.
     *
     * @param gitabaseId The Gitabase ID
     * @param bookPreview The book to load
     * @return Flow of LoadBookDetailState emitting progressive loading states
     */
    fun execute(gitabaseId: GitabaseID, bookPreview: BookPreview): Flow<LoadBookDetailState> = flow {
        try {
            // Stage 1: Load metadata without bitmaps
            emit(LoadBookDetailState(imageLoadingState = ImageLoadingState.LoadingMetadata))

            val result = textsRepository.getBookDetail(gitabaseId, bookPreview, extractImages = false)

            if (result.isFailure) {
                emit(LoadBookDetailState(
                    error = result.exceptionOrNull()?.message ?: "Failed to load book detail"
                ))
                return@flow
            }

            var bookDetail = result.getOrThrow()

            // Extract image list
            val allImages = bookDetail.imageTabs?.flatMap { it.images } ?: emptyList()

            // Handle edge case: no images
            if (allImages.isEmpty()) {
                emit(LoadBookDetailState(
                    bookDetail = bookDetail,
                    imageLoadingState = ImageLoadingState.Ready(imageFilesExtracted = true)
                ))
                return@flow
            }

            // Check if images are already extracted
            val imageFilesExtracted = imageFilesRepository.checkImageFilesExtracted(gitabaseId, allImages).first()

            if (imageFilesExtracted) {
                // Images already extracted, emit ready state
                emit(LoadBookDetailState(
                    bookDetail = bookDetail,
                    imageLoadingState = ImageLoadingState.Ready(imageFilesExtracted = true)
                ))
            } else {
                // Images need extraction
                emit(LoadBookDetailState(
                    bookDetail = bookDetail,
                    imageLoadingState = ImageLoadingState.MetadataReady
                ))

                // Stage 2: Extract images
                val extractResult = textsRepository.getBookDetail(gitabaseId, bookPreview, extractImages = true)

                if (extractResult.isFailure) {
                    emit(LoadBookDetailState(
                        bookDetail = bookDetail,
                        imageLoadingState = ImageLoadingState.Ready(imageFilesExtracted = false),
                        error = extractResult.exceptionOrNull()?.message ?: "Failed to extract images"
                    ))
                    return@flow
                }

                bookDetail = extractResult.getOrThrow()

                // Extract images from the new bookDetail (which now has bitmaps)
                val extractedImages = bookDetail.imageTabs?.flatMap { it.images } ?: emptyList()
                
                // checkImageFilesExtracted will actually save the files to disk
                val filesExtracted = imageFilesRepository.checkImageFilesExtracted(gitabaseId, extractedImages).first()

                emit(LoadBookDetailState(
                    bookDetail = bookDetail,
                    imageLoadingState = ImageLoadingState.Ready(imageFilesExtracted = filesExtracted)
                ))
            }
        } catch (e: Exception) {
            emit(LoadBookDetailState(
                error = e.message ?: "Unknown error occurred"
            ))
        }
    }
}

/**
 * State representing the loading progress of book detail.
 * Designed to support parallel loading of images and notes.
 */
data class LoadBookDetailState(
    val bookDetail: BookDetail? = null,
    val imageLoadingState: ImageLoadingState = ImageLoadingState.LoadingMetadata,
    val notesLoadingState: NotesLoadingState = NotesLoadingState.NotStarted,
    val error: String? = null
)

/**
 * State representing the image loading progress.
 */
sealed class ImageLoadingState {
    object LoadingMetadata : ImageLoadingState()
    object MetadataReady : ImageLoadingState()  // Images need extraction, show placeholders
    data class Ready(val imageFilesExtracted: Boolean) : ImageLoadingState()
}

/**
 * State representing the notes loading progress (for future use).
 */
sealed class NotesLoadingState {
    object NotStarted : NotesLoadingState()
    object Loading : NotesLoadingState()
    // Future: data class Ready(val notes: List<Note>) : NotesLoadingState()
}

