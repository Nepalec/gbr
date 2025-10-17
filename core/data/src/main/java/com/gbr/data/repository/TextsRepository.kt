package com.gbr.data.repository

import com.gbr.model.book.Book
import com.gbr.model.gitabase.GitabaseID

/**
 * Repository interface for accessing text content from gitabase databases.
 * Provides methods to query books and other text content from cached database connections.
 */
interface TextsRepository {
    /**
     * Gets all books from the specified gitabase.
     * Uses cached database connection if available for optimal performance.
     * 
     * @param gitabaseId The ID of the gitabase to query
     * @return Result containing the list of books or an error
     */
    suspend fun getAllBooks(gitabaseId: GitabaseID): Result<List<Book>>
}
