package com.gbr.data.repository

import android.content.Context
import com.gbr.data.R
import com.gbr.data.database.GitabaseDatabaseManager
import com.gbr.model.book.BookPreview
import com.gbr.model.book.BookDetail
import com.gbr.model.book.BookStructure
import com.gbr.model.book.ImageFileItem
import com.gbr.model.book.BookImageTab
import com.gbr.model.gitabase.GitabaseID
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of TextsRepository that uses cached database connections
 * to efficiently query text content from multiple gitabases.
 */
@Singleton
class TextsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gitabasesRepository: GitabasesRepository,
    private val databaseManager: GitabaseDatabaseManager
) : TextsRepository {

    override suspend fun getAllBooks(gitabaseId: GitabaseID): Result<List<BookPreview>> {
        return withContext(Dispatchers.IO) {
            try {
                // Verify gitabase exists in repository
                val gitabaseExists = gitabasesRepository.getAllGitabases()
                    .any { it.id == gitabaseId }

                if (!gitabaseExists) {
                    return@withContext Result.failure(
                        IllegalArgumentException("Gitabase not found: $gitabaseId")
                    )
                }

                // Get database from manager (uses cache for optimal performance)
                val database = databaseManager.getDatabase(gitabaseId)

                // Query books using DAO (already returns domain models)
                val books = database.bookDao().getAllBookPreviews().first()

                Result.success(books)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getBookDetail(gitabaseId: GitabaseID, book: BookPreview, extractImages: Boolean): Result<BookDetail> {
        return withContext(Dispatchers.IO) {
            try {
                val database = databaseManager.getDatabase(gitabaseId)

                // Get cover image for this specific book
                val coverImage = database.bookDao().getBookCoverImageBitmap(book)
                val imagesMap = database.bookDao().getBookImagesFileNames(book, gitabaseId, extractImages).first()

                // Convert Map<Int, List<ImageFileItem>> to List<BookImageTab>
                val imageTabs = imagesMap?.map { (kind, imageList) ->
                    val imageType = when (kind) {
                        1 -> com.gbr.model.gitabase.ImageType.PICTURE
                        2 -> com.gbr.model.gitabase.ImageType.CARD
                        3 -> com.gbr.model.gitabase.ImageType.DIAGRAM
                        4 -> com.gbr.model.gitabase.ImageType.FRESCO
                        else -> com.gbr.model.gitabase.ImageType.PICTURE
                    }
                    
                    val tabTitleResId = when (imageType) {
                        com.gbr.model.gitabase.ImageType.PICTURE -> R.string.image_tab_pictures
                        com.gbr.model.gitabase.ImageType.CARD -> R.string.image_tab_cards
                        com.gbr.model.gitabase.ImageType.DIAGRAM -> R.string.image_tab_diagrams
                        com.gbr.model.gitabase.ImageType.FRESCO -> R.string.image_tab_frescoes
                    }
                    
                    val tabTitle = context.getString(tabTitleResId)
                    
                    BookImageTab(
                        type = imageType,
                        images = imageList,
                        tabTitle = tabTitle
                    )
                }

                val bookDetail = if (book.structure == BookStructure.CHAPTERS) {
                    val chapters = database.bookDao().getBookContentsChapters(book).first()
                    BookDetail(
                        book = book,
                        coverImageBitmap = coverImage,
                        chapters = chapters,
                        texts = null,
                        imageTabs = imageTabs
                    )
                } else {
                    val booktexts = database.bookDao().getBookContentsTexts(book).first()
                    BookDetail(
                        book = book,
                        coverImageBitmap = coverImage,
                        chapters = null,
                        texts = booktexts,
                        imageTabs = imageTabs
                    )
                }

                Result.success(bookDetail)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

}
