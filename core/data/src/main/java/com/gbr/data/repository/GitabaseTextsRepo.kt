package com.gbr.data.repository

import com.gbr.data.model.BookItem
import com.gbr.model.gitabase.GitabaseID
import kotlinx.coroutines.flow.Flow

interface GitabaseTextsRepo {
    suspend fun getBooks(gitabaseId: GitabaseID): Flow<List<BookItem>>
}
