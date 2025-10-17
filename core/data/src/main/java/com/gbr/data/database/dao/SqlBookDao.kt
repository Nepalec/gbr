package com.gbr.data.database.dao

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.gbr.model.book.Book
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

    fun getAllBooks(): Flow<List<Book>> = flow {
        val books = withContext(Dispatchers.IO) {
            val bookList = mutableListOf<Book>()

            try {
                val cursor: Cursor? = database.rawQuery("SELECT * FROM books", null)
                cursor?.use { c ->
                    while (c.moveToNext()) {
                        try {
                            val book = createBookFromCursor(c)
                            bookList.add(book)
                        } catch (e: Exception) {
                            // Skip invalid rows, continue processing
                        }
                    }
                }
            } catch (e: Exception) {
                // Return empty list on database errors
            }

            bookList
        }
        emit(books)
    }

    suspend fun getBookById(id: Int): Book? = withContext(Dispatchers.IO) {
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

    suspend fun getBooksByCompareCode(compareCode: String): List<Book> = withContext(Dispatchers.IO) {
        val books = mutableListOf<Book>()

        try {
            val cursor: Cursor? = database.rawQuery(
                "SELECT * FROM books WHERE compare_code = ?",
                arrayOf(compareCode)
            )

            cursor?.use { c ->
                while (c.moveToNext()) {
                    try {
                        val book = createBookFromCursor(c)
                        books.add(book)
                    } catch (e: Exception) {
                        // Skip invalid rows, continue processing
                    }
                }
            }
        } catch (e: Exception) {
            // Log error if needed
        }

        books
    }

    suspend fun getBooksByType(type: String): List<Book> = withContext(Dispatchers.IO) {
        val books = mutableListOf<Book>()

        try {
            val cursor: Cursor? = database.rawQuery(
                "SELECT * FROM books WHERE type = ?",
                arrayOf(type)
            )

            cursor?.use { c ->
                while (c.moveToNext()) {
                    try {
                        val book = createBookFromCursor(c)
                        books.add(book)
                    } catch (e: Exception) {
                        // Skip invalid rows, continue processing
                    }
                }
            }
        } catch (e: Exception) {
            // Log error if needed
        }

        books
    }

    /**
     * Creates a domain Book model from a Cursor positioned at a valid row.
     */
    private fun createBookFromCursor(cursor: Cursor): Book {
        return Book(
            id = cursor.getIntOrNull("_id") ?: 0,
            sort = cursor.getIntOrNull("sort"),
            author = cursor.getStringOrNull("author"),
            title = cursor.getStringOrNull("title"),
            desc = cursor.getStringOrNull("desc"),
            type = cursor.getStringOrNull("type"),
            levels = cursor.getIntOrNull("levels")?.toDouble(), // Convert Int to Double
            hasSanskrit = cursor.getIntOrNull("hasSanskrit") ?: 0,
            hasPurport = cursor.getIntOrNull("hasPurport") ?: 1,
            hasColorStructure = cursor.getIntOrNull("hasColorStructure"),
            isSongBook = cursor.getIntOrNull("isSongBook") ?: 0,
            textSize = cursor.getIntOrNull("text_size"),
            purportSize = cursor.getIntOrNull("purport_size"),
            textBeginRaw = cursor.getIntOrNull("text_begin_raw") ?: 0,
            textEndRaw = cursor.getIntOrNull("text_end_raw") ?: 0,
            webAbbrev = cursor.getStringOrNull("web_abbrev"),
            compareCode = cursor.getStringOrNull("compare_code"),
            issue = cursor.getStringOrNull("issue"),
            isSimple = cursor.getIntOrNull("isSimple") ?: 1
        )
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
