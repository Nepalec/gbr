package com.gbr.data.database.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.gbr.model.book.BookPreview
import com.gbr.model.book.BookStructure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

/**
 * SQLite implementation for Book operations using raw SQL queries.
 * Works directly with domain models, bypassing Room entities.
 */
class SqlBookDao(
    private val database: SQLiteDatabase
) {

    fun getAllBooks(): Flow<List<BookPreview>> = flow {
        val books = withContext(Dispatchers.IO) {
            val bookDetailList = mutableListOf<BookPreview>()

            try {
                val cursor: Cursor? = database.rawQuery("""
                    SELECT 
                      b._id as book_id,
                      b.sort as book_sort,
                      b.title as book_title,
                      b.author,
                      b.desc,
                      b.type,
                      b.levels,
                      b.web_abbrev,
                      s._id as song_id,
                      s.song as song_number,
                      s.songname,
                      s.sort as song_sort,
                      s.colorBackgnd,
                      s.colorForegnd
                    FROM books b
                    LEFT JOIN songs s ON b._id = s.book_id
                    ORDER BY b.sort, s.sort
                """.trimIndent(), null)
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
                // Return empty list on database errors
            }

            bookDetailList
        }
        emit(books)
    }

    suspend fun getBookById(id: Int): BookPreview? = withContext(Dispatchers.IO) {
        try {
            val cursor: Cursor? = database.rawQuery(
                "SELECT * FROM books WHERE _id = ?",
                arrayOf(id.toString())
            )

            cursor?.use { c ->
                if (c.moveToFirst()) {
                    return@withContext createBookFromCursor(c)
                }
            }
        } catch (e: Exception) {
            // Log error if needed
        }

        null
    }

    suspend fun getBooksByCompareCode(compareCode: String): List<BookPreview> = withContext(Dispatchers.IO) {
        val bookDetails = mutableListOf<BookPreview>()

        try {
            val cursor: Cursor? = database.rawQuery(
                "SELECT * FROM books WHERE compare_code = ?",
                arrayOf(compareCode)
            )

            cursor?.use { c ->
                while (c.moveToNext()) {
                    try {
                        val book = createBookFromCursor(c)
                        bookDetails.add(book)
                    } catch (e: Exception) {
                        // Skip invalid rows, continue processing
                    }
                }
            }
        } catch (e: Exception) {
            // Log error if needed
        }

        bookDetails
    }

    suspend fun getBooksByType(type: String): List<BookPreview> = withContext(Dispatchers.IO) {
        val bookDetails = mutableListOf<BookPreview>()

        try {
            val cursor: Cursor? = database.rawQuery(
                "SELECT * FROM books WHERE type = ?",
                arrayOf(type)
            )

            cursor?.use { c ->
                while (c.moveToNext()) {
                    try {
                        val book = createBookFromCursor(c)
                        bookDetails.add(book)
                    } catch (e: Exception) {
                        // Skip invalid rows, continue processing
                    }
                }
            }
        } catch (e: Exception) {
            // Log error if needed
        }

        bookDetails
    }

    /**
     * Creates a domain Book model from a Cursor positioned at a valid row.
     */
    private fun createBookFromCursor(cursor: Cursor): BookPreview {
        return BookPreview(
            id = cursor.getIntOrNull("_id") ?: 0,
            sort = cursor.getIntOrNull("sort") ?: 0,
            title = cursor.getStringOrNull("title") ?: "",
            author = cursor.getStringOrNull("author") ?: "",
            description = cursor.getStringOrNull("desc"),
            type = cursor.getStringOrNull("type") ?: "",
            level = cursor.getIntOrNull("levels") ?: 3,
            structure = BookStructure.fromInt(cursor.getIntOrNull("levels") ?: 3)
                ?: BookStructure.CHAPTERS,
            colorBack = null,
            colorFore = null,
            volumeGroupTitle = null,
            volumeGroupAbbrev = null,
            volumeGroupSort = null,
            volumeGroupId = null,
            volumeNumber = null
        )
    }

    /**
     * Creates a domain Book model from a Cursor with joined books and songs data.
     * Handles both books with volumes and standalone books.
     */
    private fun createBookPreviewFromJoinedCursor(cursor: Cursor): BookPreview {
        val songId = cursor.getIntOrNull("song_id")
        val bookId = cursor.getIntOrNull("book_id") ?: 0
        val bookTitle = cursor.getStringOrNull("book_title") ?: ""
        val bookAuthor = cursor.getStringOrNull("author") ?: ""
        val bookDescription = cursor.getStringOrNull("desc")
        val bookType = cursor.getStringOrNull("type") ?: ""
        val bookLevel = cursor.getIntOrNull("levels") ?: 3
        val bookSort = cursor.getIntOrNull("book_sort") ?: 0
        val bookWebAbbrev = cursor.getStringOrNull("web_abbrev")
        
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
                volumeNumber = songNumber
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
                volumeNumber = null
            )
        }
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
