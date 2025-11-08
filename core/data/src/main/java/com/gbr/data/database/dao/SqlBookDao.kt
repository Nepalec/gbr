package com.gbr.data.database.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.gbr.model.book.BookPreview
import com.gbr.model.book.BookStructure
import com.gbr.model.book.ChapterContentsItem
import com.gbr.model.book.ImageFileItem
import com.gbr.model.book.TextContentsItem
import com.gbr.model.book.TextItem
import com.gbr.model.gitabase.GitabaseID
import com.gbr.model.gitabase.ImageFormat
import com.gbr.model.gitabase.ImageType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

/**
 * SQLite implementation for Book operations using raw SQL queries.
 * Works directly with domain models
 * BookPreview works as a key
 */
class SqlBookDao(
    private val database: SQLiteDatabase
) {
    fun getBookContentsChapters(book: BookPreview): Flow<List<ChapterContentsItem>> = flow {
        val chapters = withContext(Dispatchers.IO) {
            val chapterList = mutableListOf<ChapterContentsItem>()

            try {
                val debugQuery = if (book.isVolume) {
                    """
                        SELECT *
                        FROM chapters c
                        WHERE c.book_id = ${book.volumeGroupId} AND c.song = ${book.volumeNumber}
                        ORDER BY c.number
                    """.trimIndent()
                } else {
                    """
                        SELECT *
                        FROM chapters c
                        WHERE c.book_id = ${book.id}
                        ORDER BY c.number
                    """.trimIndent()
                }
                val cursor: Cursor? = database.rawQuery(debugQuery, null)

                cursor?.use { c ->
                    while (c.moveToNext()) {
                        try {
                            val chapter = ChapterContentsItem(
                                id = c.getIntOrNull("_id") ?: 0,
                                book = book,
                                number = c.getIntOrNull("number") ?: 0,
                                title = c.getStringOrNull("title") ?: "",
                                intro = c.getStringOrNull("desc"),
                            )
                            chapterList.add(chapter)
                        } catch (e: Exception) {
                            // Skip invalid rows, continue processing
                        }
                    }
                }
            } catch (e: Exception) {
                // Return empty list on database errors
            }

            chapterList
        }
        emit(chapters)
    }

    fun getBookContentsTexts(book: BookPreview, chapterNumber: String? = null): Flow<List<TextContentsItem>> = flow {
        val texts = withContext(Dispatchers.IO) {
            val textList = mutableListOf<TextContentsItem>()

            try {
                val debugQuery = """
                    SELECT *
                    FROM textnums t
                    WHERE t.book_id = ${book.id}
                    ORDER BY t._id
                """.trimIndent()
                val cursor: Cursor? = database.rawQuery(debugQuery, null)

                cursor?.use { c ->
                    while (c.moveToNext()) {
                        try {
                            val textContentsItem = TextContentsItem(
                                id = c.getIntOrNull("_id") ?: 0,
                                book = book,
                                textNumber = c.getStringOrNull("txt_no") ?: "",
                                title = c.getStringOrNull("preview") ?: ""
                            )
                            textList.add(textContentsItem)
                        } catch (e: Exception) {
                            // Skip invalid rows, continue processing
                        }
                    }
                }
            } catch (e: Exception) {
                // Return empty list on database errors
            }

            textList
        }
        emit(texts)
    }

    fun getChapterTexts(book: BookPreview, chapterNumber: Int): Flow<List<TextItem>> = flow {
        val texts = withContext(Dispatchers.IO) {
            val textList = mutableListOf<TextItem>()

            try {

                val debugQuery = if (book.isVolume) {
                    """
                        SELECT *
                        FROM textnums t
                        WHERE t.book_id = ${book.volumeGroupId}
                        AND t.song = ${book.volumeNumber}
                        AND t.ch_no = ${chapterNumber}
                        ORDER BY t._id
                    """.trimIndent()
                } else {
                    """
                   SELECT *
                        FROM textnums t
                        WHERE t.book_id = ${book.id}
                        AND t.ch_no = ${chapterNumber}
                        ORDER BY t._id
                    """.trimIndent()
                }

                val cursor: Cursor? = database.rawQuery(debugQuery, null)

                cursor?.use { c ->
                    while (c.moveToNext()) {
                        try {
                            val textItem = TextItem(
                                id = c.getIntOrNull("_id") ?: 0,
                                book = book,
                                chapterNumber = c.getIntOrNull("ch_no") ?: chapterNumber,
                                textNumber = c.getStringOrNull("txt_no") ?: "",
                                title = c.getStringOrNull("preview") ?: "",
                                titleSanskrit = c.getStringOrNull("title_sanskrit") ?: ""
                            )
                            textList.add(textItem)
                        } catch (e: Exception) {
                            // Skip invalid rows, continue processing
                        }
                    }
                }
            } catch (e: Exception) {
                // Return empty list on database errors
            }

            textList
        }
        emit(texts)
    }

    fun getAllBookPreviews(): Flow<List<BookPreview>> = flow {
        val books = withContext(Dispatchers.IO) {
            val bookDetailList = mutableListOf<BookPreview>()

            try {
                val debugQuery = """
                SELECT
                  b.*,                        -- all columns from the books table
                  s._id AS song_id,
                  s.song AS song_number,
                  s.songname,
                  s.sort AS song_sort,
                  s.colorBackgnd,
                  s.colorForegnd
                FROM books b
                LEFT JOIN songs s ON b._id = s.book_id
                ORDER BY b.sort, s.sort;
                """.trimIndent()
                val cursor: Cursor? = database.rawQuery(debugQuery, null)
                cursor?.use { c ->
                    while (c.moveToNext()) {
                        try {
                            val book = createBookPreviewFromJoinedCursor(c)
                            bookDetailList.add(book)
                        } catch (e: Exception) {
                            // Skip invalid rows, continue processing
                        }
                    }
                }
            } catch (e: Exception) {
                emptyList<BookPreview>()
            }

            bookDetailList
        }
        emit(books)
    }

    suspend fun getBookCoverImageBitmap(book: BookPreview): String? = withContext(Dispatchers.IO) {
        try {
            val debugQuery = if (book.isVolume) {
                "select * from image_nums nums LEFT JOIN images imgs " +
                    "on nums.image_id=imgs.image_id  " +
                    "LEFT JOIN textnums txt on txt.text_id=nums.text_id " +
                    "WHERE nums.bid='${book.volumeGroupId}' AND nums.sid='${book.volumeNumber}' AND kind='11'"
            } else {
                "select * from image_nums nums LEFT JOIN images imgs on nums.image_id=imgs.image_id  " +
                    "LEFT JOIN textnums txt on txt.text_id=nums.text_id " +
                    "WHERE nums.bid='${book.id}' AND kind='10'"
            }

            val debugCursor: Cursor? =
                database.rawQuery(debugQuery, null)

            debugCursor?.use { c ->
                if (c.moveToNext()) {
                    return@withContext c.getStringOrNull("content")
                }
            }

        } catch (e: Exception) {
        }

        null
    }

//    fun getBookChapters(book: BookPreview): Flow<List<Chapter>> = flow {}
//    fun getBookTexts(book: BookPreview): Flow<List<TextItem>> = flow {}
//    fun getBookImagesFileNames(book: BookPreview): Flow<List<TextImage>> = flow {}


    /**
     * Creates a domain Book model from a Cursor with joined books and songs data.
     * Handles both books with volumes and standalone books.
     */
    private fun createBookPreviewFromJoinedCursor(cursor: Cursor): BookPreview {
        val songId = cursor.getIntOrNull("song_id")
        // Since query uses b.*, column names are the actual table column names (not aliased)
        val bookId = cursor.getIntOrNull("_id") ?: 0
        val bookTitle = cursor.getStringOrNull("title") ?: ""
        val bookAuthor = cursor.getStringOrNull("author") ?: ""
        val bookDescription = cursor.getStringOrNull("desc")
        val bookType = cursor.getStringOrNull("type") ?: ""
        val bookLevel = cursor.getIntOrNull("levels") ?: 3
        val bookSort = cursor.getIntOrNull("sort") ?: 0
        val bookWebAbbrev = cursor.getStringOrNull("web_abbrev")

        // Get additional metadata fields
        val hasSanskrit = cursor.getIntOrNull("hasSanskrit") == 1
        val isSimple = cursor.getIntOrNull("isSimple") == 1
        val code = cursor.getStringOrNull("compare_code") ?: ""

        return if (songId != null) {
            // Book WITH volumes - create BookPreview for the volume
            val songName = cursor.getStringOrNull("songname") ?: ""
            val songSort = cursor.getIntOrNull("song_sort") ?: 0
            val songNumber = cursor.getStringOrNull("song_number")?.toIntOrNull()
            val colorBack = cursor.getStringOrNull("colorBackgnd")?.takeIf { it.isNotEmpty() }
            val colorFore = cursor.getStringOrNull("colorForegnd")?.takeIf { it.isNotEmpty() }

            BookPreview(
                id = songId,
                sort = songSort,
                title = songName,
                author = bookAuthor,
                description = bookDescription,
                type = bookType,
                level = bookLevel,
                structure = BookStructure.fromInt(bookLevel) ?: BookStructure.CHAPTERS,
                colorBack = colorBack,
                colorFore = colorFore,
                volumeGroupTitle = bookTitle,
                volumeGroupAbbrev = bookWebAbbrev,
                volumeGroupSort = bookSort,
                volumeGroupId = bookId,
                volumeNumber = songNumber,
                hasSanskrit = hasSanskrit,
                isSimple = isSimple,
                code = code
            )
        } else {
            // Book WITHOUT volumes - create BookPreview for the book itself
            BookPreview(
                id = bookId,
                sort = bookSort,
                title = bookTitle,
                author = bookAuthor,
                description = bookDescription,
                type = bookType,
                level = bookLevel,
                structure = BookStructure.fromInt(bookLevel) ?: BookStructure.CHAPTERS,
                colorBack = null,
                colorFore = null,
                volumeGroupTitle = null,
                volumeGroupAbbrev = null,
                volumeGroupSort = null,
                volumeGroupId = null,
                volumeNumber = null,
                hasSanskrit = hasSanskrit,
                isSimple = isSimple,
                code = code
            )
        }
    }


    fun getBookImagesFileNames(
        book: BookPreview,
        gitabaseId: GitabaseID,
        extractImages: Boolean
    ): Flow<Map<Int, List<ImageFileItem>>?> = flow {
        val images = withContext(Dispatchers.IO) {
            val imageList = mutableListOf<Pair<Int, ImageFileItem>>()

            try {
                // Build the query with optional LEFT JOIN for chapters when book.hasChapters is true
                // Also join textnums for fallback description
                val chaptersJoin = if (book.hasChapters) {
                    """
                        LEFT JOIN chapters ch ON ch.number = nums.cid
                        AND ch.book_id = ${if (book.isVolume) book.volumeGroupId else book.id}
                        ${if (book.isVolume) "AND ch.song = ${book.volumeNumber}" else ""}
                    """.trimIndent()
                } else {
                    ""
                }

                val textnumsJoin = "LEFT JOIN textnums txt ON txt.text_id = nums.text_id"
                val imagesJoin = if (extractImages) {
                    "LEFT JOIN images img ON img.image_id = nums.image_id"
                } else {
                    ""
                }

                val joinClause = buildString {
                    if (book.hasChapters) {
                        append(chaptersJoin)
                        append("\n                        ")
                    }
                    append(textnumsJoin)
                    if (extractImages) {
                        append("\n                        ")
                        append(imagesJoin)
                    }
                }

                val selectClause = buildString {
                    append("SELECT nums.image_id, nums.kind, nums.type, nums.cid, ")
                    if (book.hasChapters) {
                        append("ch.title as chapter_title, ")
                    } else {
                        append("NULL as chapter_title, ")
                    }
                    append("nums.tnum as text_number, nums.desc as image_description, txt.preview as text_preview")
                    if (extractImages) {
                        append(", img.content as image_content")
                    }
                }

                val debugQuery = if (book.isVolume) {
                    """
                        $selectClause
                        FROM image_nums nums
                        $joinClause
                        WHERE nums.bid = ${book.volumeGroupId} AND nums.sid = ${book.volumeNumber} AND nums.kind < 10
                        ORDER BY nums.kind, nums.image_id
                    """.trimIndent()
                } else {
                    """
                        $selectClause
                        FROM image_nums nums
                        $joinClause
                        WHERE nums.bid = ${book.id} AND nums.kind < 10
                        ORDER BY nums.kind, nums.image_id
                    """.trimIndent()
                }
                val cursor: Cursor? = database.rawQuery(debugQuery, null)

                cursor?.use { c ->
                    while (c.moveToNext()) {
                        try {
                            val kind = c.getIntOrNull("kind") ?: 1
                            val typeValue = c.getIntOrNull("type") ?: 3

                            val imageType = when (kind) {
                                1 -> ImageType.PICTURE
                                2 -> ImageType.CARD
                                3 -> ImageType.DIAGRAM
                                4 -> ImageType.FRESCO
                                else -> ImageType.PICTURE
                            }

                            val imageFormat = when (typeValue) {
                                1 -> ImageFormat.GIF
                                2 -> ImageFormat.PNG
                                3 -> ImageFormat.JPEG
                                4 -> ImageFormat.SVG
                                else -> ImageFormat.JPEG
                            }

                            // Get imageDescription with fallback: use nums.desc if not empty, otherwise use txt.preview
                            val imageDescription = c.getStringOrNull("image_description")
                                ?.takeIf { it.isNotEmpty() }
                                ?: c.getStringOrNull("text_preview")

                            // Get bitmap from images table if extractImages is true
                            // BLOB contains UTF-8 bytes of Base64 string, convert to String (don't encode)
                            val bitmap = if (extractImages) {
                                val imageContent = c.getBlobOrNull("image_content")
                                if (imageContent != null) {
                                    String(imageContent, Charsets.UTF_8)
                                } else {
                                    null
                                }
                            } else {
                                null
                            }

                            // Construct full image path: gitabaseId.key/imageId.format.fileExtension
                            // This will be combined with context.filesDir in the repository layer
                            val imageId = c.getStringOrNull("image_id") ?: ""
                            val fullImagePath = if (imageId.isNotEmpty()) {
                                "${gitabaseId.key}/${imageId}.${imageFormat.fileExtension}"
                            } else {
                                null
                            }

                            val imageFileItem = ImageFileItem(
                                id = imageId,
                                format = imageFormat,
                                bitmap = bitmap,
                                fullImagePath = fullImagePath,
                                type = imageType,
                                chapterNumber = c.getIntOrNull("cid"),
                                textNumber = c.getStringOrNull("text_number") ?: "",
                                chapterTitle = c.getStringOrNull("chapter_title"),
                                imageDescription = imageDescription
                            )
                            imageList.add(kind to imageFileItem)
                        } catch (e: Exception) {
                            // Skip invalid rows, continue processing
                        }
                    }
                }
            } catch (e: Exception) {
                // Return empty list on database errors
            }

            // Group by kind
            imageList
                .groupBy({ it.first }, { it.second })
                .takeIf { it.isNotEmpty() }
        }
        emit(images)
    }

}

/**
 * Extension functions for safe cursor operations
 */
private fun Cursor.getIntOrNull(columnName: String): Int? {
    val columnIndex = getColumnIndex(columnName)
    return if (columnIndex >= 0 && !isNull(columnIndex)) {
        getInt(columnIndex)
    } else null
}

private fun Cursor.getStringOrNull(columnName: String): String? {
    val columnIndex = getColumnIndex(columnName)
    return if (columnIndex >= 0 && !isNull(columnIndex)) {
        getString(columnIndex)
    } else null
}

private fun Cursor.getBlobOrNull(columnName: String): ByteArray? {
    val columnIndex = getColumnIndex(columnName)
    return if (columnIndex >= 0 && !isNull(columnIndex)) {
        getBlob(columnIndex)
    } else null
}
