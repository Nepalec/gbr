package com.gbr.data.repository

import android.content.Context
import com.gbr.common.strings.StringProvider
import com.gbr.data.R
import com.gbr.data.database.GitabaseDatabaseManager
import com.gbr.model.book.BookDetail
import com.gbr.model.book.BookImageTab
import com.gbr.model.book.BookPreview
import com.gbr.model.book.BookStructure
import com.gbr.model.book.TextItem
import com.gbr.model.gitabase.GitabaseID
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
    private val databaseManager: GitabaseDatabaseManager,
    private val stringProvider: StringProvider
) : TextsRepository {

    companion object {
        private const val MAX_CACHE_SIZE = 5
    }

    // LinkedHashMap with access-order mode (true) for LRU behavior
    // When accessed, entries move to the end of the map
    // The first entry is always the least recently used
    private val booksCache = LinkedHashMap<GitabaseID, List<BookPreview>>(
        MAX_CACHE_SIZE,
        0.75f,  // load factor
        true     // access-order mode (vs insertion-order)
    )

    // Mutex for thread-safe cache access (allows suspending functions)
    private val cacheMutex = Mutex()

    override suspend fun getAllBooks(gitabaseId: GitabaseID): Result<List<BookPreview>> {
        return withContext(Dispatchers.IO) {
            cacheMutex.withLock {
                try {
                    // Check cache first
                    val cachedBooks = booksCache[gitabaseId]
                    if (cachedBooks != null) {
                        return@withContext Result.success(cachedBooks)
                    }

                    // Verify gitabase exists in repository
                    val gitabaseExists = gitabasesRepository.getAllGitabases()
                        .any { it.id == gitabaseId }

                    if (!gitabaseExists) {
                        return@withContext Result.failure(
                            IllegalArgumentException(
                                stringProvider.getString(
                                    R.string.error_gitabase_not_found,
                                    gitabaseId.toString()
                                )
                            )
                        )
                    }

                    // Get database from manager (uses cache for optimal performance)
                    val database = databaseManager.getDatabase(gitabaseId)

                    // Query books using DAO (already returns domain models)
                    val books = database.bookDao().getAllBookPreviews().first()

                    // If cache is full, evict the least recently used entry
                    if (booksCache.size >= MAX_CACHE_SIZE) {
                        evictLRU()
                    }

                    // Store in cache
                    booksCache[gitabaseId] = books

                    Result.success(books)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
        }
    }

    override suspend fun getBookPreviewById(gitabaseId: GitabaseID, id: Int): Result<BookPreview?> {
        return withContext(Dispatchers.IO) {
            cacheMutex.withLock {
                try {
                    // Check cache first
                    val cachedBooks = booksCache[gitabaseId]
                    if (cachedBooks != null) {
                        val book = cachedBooks.find { book -> book.id == id }
                        return@withContext Result.success(book)
                    }

                    // Verify gitabase exists in repository
                    val gitabaseExists = gitabasesRepository.getAllGitabases()
                        .any { it.id == gitabaseId }

                    if (!gitabaseExists) {
                        return@withContext Result.failure(
                            IllegalArgumentException(
                                stringProvider.getString(
                                    R.string.error_gitabase_not_found,
                                    gitabaseId.toString()
                                )
                            )
                        )
                    }

                    // Get database from manager (uses cache for optimal performance)
                    val database = databaseManager.getDatabase(gitabaseId)

                    // Query books using DAO (already returns domain models)
                    val books = database.bookDao().getAllBookPreviews().first()

                    // If cache is full, evict the least recently used entry
                    if (booksCache.size >= MAX_CACHE_SIZE) {
                        evictLRU()
                    }

                    // Store in cache
                    booksCache[gitabaseId] = books

                    // Find book by id
                    val book = books.find { book -> book.id == id }

                    Result.success(book)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
        }
    }

    override suspend fun getBookDetail(
        gitabaseId: GitabaseID,
        book: BookPreview,
        extractImages: Boolean
    ): Result<BookDetail> {
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

    override suspend fun getChapterTexts(
        gitabaseId: GitabaseID,
        bookPreview: BookPreview,
        chapterNumber: Int
    ): Result<List<TextItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val database = databaseManager.getDatabase(gitabaseId)
                val texts = database.bookDao().getChapterTexts(bookPreview, chapterNumber).first()
                Result.success(texts)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Evicts the least recently used entry from the cache.
     * The first entry in LinkedHashMap (access-order mode) is the LRU.
     * Must be called within cacheMutex.withLock { } block.
     */
    private fun evictLRU() {
        val lruEntry = booksCache.entries.firstOrNull()
        if (lruEntry != null) {
            booksCache.remove(lruEntry.key)
        }
    }

    /**
     * Invalidates the cache for a specific gitabase.
     * Useful when a gitabase is deleted or updated.
     *
     * @param gitabaseId The ID of the gitabase to invalidate
     */
    suspend fun invalidateCache(gitabaseId: GitabaseID) {
        cacheMutex.withLock {
            booksCache.remove(gitabaseId)
        }
    }

    /**
     * Clears all cached book previews.
     * Should be called when all caches need to be refreshed.
     */
    suspend fun clearCache() {
        cacheMutex.withLock {
            booksCache.clear()
        }
    }

}
