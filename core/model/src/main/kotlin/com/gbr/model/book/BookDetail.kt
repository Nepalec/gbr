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
    val fullImagePath: String?,
    val type: ImageType,
    val chapterNumber: Int?,
    val textNumber: String,
    val chapterTitle: String?,
    val imageDescription: String?
)

/**
 * Chapter information
 */
data class ChapterContentsItem(
    val id: Int,
    val book: BookPreview,
    val number: Int,
    val title: String,
    val intro: String?
)

/**
 * Text item
 */
data class TextContentsItem(
    val id: Int,
    val book: BookPreview,
    val textNumber: String,
    val title: String
)

data class TextPreviewItem(
    val id: Int,
    val book: BookPreview,
    val chapterNumber: Int,
    val textNumber: String,
    val title: String,
    val textId: String,
)

data class TextDetailItem(
    val preview: TextPreviewItem,
    val sanskrit: String,
    val translit: String,
    val wordByword: String,
    val comment: String,
    val textSeqNo: Int,
    val textOffset: Int,
    val textSize: Int,
    val numberOfImages: Int
){
    val translation: String
        get() = preview.title
}