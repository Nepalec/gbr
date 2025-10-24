package com.gbr.data.repository

import android.content.Context
import com.gbr.data.database.GitabaseDatabaseManager
import com.gbr.model.book.BookPreview
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
                val books = database.bookDao().getAllBooks().first()

                Result.success(books)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
