package com.gbr.data.repository

import com.gbr.model.book.BookDetail
import com.gbr.model.book.BookPreview
import com.gbr.model.book.TextDetailItem
import com.gbr.model.book.TextPreviewItem
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
    ): Result<List<TextPreviewItem>>

    suspend fun getBookTextsCount(
        gitabaseId: GitabaseID,
        bookPreview: BookPreview
    ): Result<Int>

    suspend fun getTextByIndex(
        gitabaseId: GitabaseID,
        bookPreview: BookPreview,
        textIndex: Int
    ): Result<TextDetailItem>

    suspend fun getTextsByIndexRange(
        gitabaseId: GitabaseID,
        bookPreview: BookPreview,
        startIndex: Int,
        endIndex: Int
    ): Result<List<TextDetailItem>>

    suspend fun findTextIndexByTextNumber(
        gitabaseId: GitabaseID,
        bookPreview: BookPreview,
        chapterNumber: Int?,
        textNumber: String
    ): Result<Int>
}