package com.gbr.data.datasource

import com.gbr.data.database.GitabaseDatabase
import com.gbr.data.dbmanager.GitabaseManager
import com.gbr.model.gitabase.BookItem
import com.gbr.model.gitabase.GitabaseID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// core/data/src/main/java/com/gitabase/core/data/datasource/local/LocalGitabaseDataSource.kt
@Singleton
class LocalGitabaseDataSource @Inject constructor(
    private val gitabaseManager: GitabaseManager,
    private val gitabaseId: GitabaseID
) {

    // 🎯 Get database instance once during initialization
    private val database: GitabaseDatabase by lazy {
        gitabaseManager.getDatabase(gitabaseId)
    }

    /**
     * Get all books from the specific database
     */
    suspend fun getAllBooks(): Result<List<BookItem>> {
        return try {
            // 🎯 Use cached database instance
            val bookEntities = database.bookDao().getAllBooks()

            val books = bookEntities.map { entity ->
                BookItem(
                    id = entity._id,
                    title = entity.title ?: "",
                    author = entity.author ?: "",
                    issue = entity.issue ?: "",
                    abbreviation = entity.web_abbrev ?: "",
                    code = entity.type ?: "",
                    compareCode = entity.compare_code ?: "",
                    levels = entity.levels ?: 0,
                    isSimple = entity.isSimple == 1,
                    isSongBook = entity.isSongBook == 1,
                    hasSanskrit = entity.hasSanskrit == 1,
                    hasColorStructure = entity.hasColorStructure == 1,
                    textSize = entity.text_size ?: 0,
                    commentSize = entity.purport_size ?: 0,
                    totalSize = (entity.text_size ?: 0) + (entity.purport_size ?: 0)
                )
            }

            Result.success(books)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ... rest of the methods remain the same
}
