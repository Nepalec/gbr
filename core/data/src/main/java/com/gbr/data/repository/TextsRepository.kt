package com.gbr.data.repository

import com.gbr.model.book.BookDetail
import com.gbr.model.book.BookPreview
import com.gbr.model.book.TextItem
import com.gbr.model.gitabase.GitabaseID

interface TextsRepository {
    suspend fun getAllBooks(gitabaseId: GitabaseID): Result<List<BookPreview>>
    suspend fun getBookPreviewById(gitabaseId: GitabaseID, id: Int): Result<BookPreview?>
    suspend fun getBookDetail(
        gitabaseId: GitabaseID,
        bookPreview: BookPreview,
        extractImages: Boolean
    ): Result<BookDetail>

    suspend fun getChapterTexts(
        gitabaseId: GitabaseID,
        bookPreview: BookPreview,
        chapterNumber: Int
    ): Result<List<TextItem>>
}
