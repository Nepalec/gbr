package com.gbr.model.book

import com.gbr.model.gitabase.ImageFormat
import com.gbr.model.gitabase.ImageType

class BookImageTab(
    val type: ImageType,
    val images: List<ImageFileItem>,
    val tabTitle: String
)

data class BookDetail(
    val book: BookPreview,
    val coverImageBitmap: String?,
    val chapters: List<ChapterContentsItem>? = null,
    val texts: List<TextContentsItem>? = null,
    val imageTabs: List<BookImageTab>? = null
)


data class ImageFileItem(
    val id: String,
    val format: ImageFormat,
    val bitmap: String?,
    val type: ImageType,
    val chapter: Int? //chapter number
)

/**
 * Chapter information
 */
data class ChapterContentsItem(
    val id: Int,
    val book: BookPreview,
    val number: Int,
    val title: String,
    val intro: String?,
)

/**
 * Text item
 */
data class TextContentsItem(
    val id: Int,
    val book: BookPreview,
    val textNumber: String,
    val title: String,
)
