package com.gbr.data.repository

import com.gbr.data.database.GitabaseDatabase
import com.gbr.model.gitabase.BookItem
import com.gbr.model.gitabase.GitabaseID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitabaseTextsRepoImpl @Inject constructor() : GitabaseTextsRepo {

    // Cache of open databases
    private val databaseCache = mutableMapOf<GitabaseID, GitabaseDatabase>()

    override suspend fun getBooks(gitabaseId: GitabaseID): Flow<List<BookItem>> {
        val database = getOrCreateDatabase(gitabaseId)
        val bookDao = database.bookDao()

        return bookDao.getAllBooks().map { books ->
            books.map { book ->
                BookItem(
                    id = book._id,
                    uid = book._id.toString(),
                    title = book.title ?: "",
                    author = book.author ?: "",
                    abbreviation = "",
                    code = book.type ?: "",
                    compareCode = book.compare_code ?: "",
                    levels = (book.levels ?: 0).toInt(),
                    hasSanskrit = book.hasSanskrit == 1
                )
            }
        }
    }

    private suspend fun getOrCreateDatabase(gitabaseId: GitabaseID): GitabaseDatabase {
        return databaseCache.getOrPut(gitabaseId) {
            // This would need to be implemented to create database from file path
            // For now, returning a placeholder
            throw NotImplementedError("Database creation from GitabaseID needs to be implemented")
        }
    }
}
