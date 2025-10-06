package com.gbr.data.repository

import com.gbr.model.gitabase.ComparableBook
import com.gbr.model.gitabase.Gitabase


// Comparable Books Repository
interface ComparableBooksRepository {
    suspend fun getComparableBooks(
        currentBook: BookItem,
        translateMode: Boolean = false
    ): Result<List<ComparableBook>>

    suspend fun scanForComparableBooks(gitabase: Gitabase): Result<List<ComparableBook>>

    suspend fun findBooksByCompareCode(
        compareCode: String,
        excludeGitabase: Gitabase? = null
    ): Result<List<ComparableBook>>
}
