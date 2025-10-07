package com.gbr.data.repository

import com.gbr.data.database.GitabaseDatabase
import com.gbr.data.model.BookItem
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
                    title = book.title ?: "",
                    author = book.author,
                    description = book.desc,
                    type = book.type,
                    levels = book.levels,
                    hasSanskrit = book.hasSanskrit == 1,
                    hasPurport = book.hasPurport == 1,
                    compareCode = book.compare_code
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
