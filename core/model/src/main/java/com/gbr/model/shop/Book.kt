package com.gbr.model.shop

data class Book(
    val id: Int,
    val code: String,
    val title: String,
    val coverImage: BookImage,
    val author: String?,
    val subtitle: String?,
    // 1 or more chapters.
    // if no chapters, then it is represented by only 1 chapter, chapter title is ignored
    // chapters can be null if getting all the books and non null if getting book detail
    val chapters: List<Chapter>?,
    //images can be null if getting all the books and non null if getting book detail
    val images: List<Chapter>?,
    val totalPages: Int,
)

data class BookImage(
    val type: ImageType,
    val source: BookImageSource,
    val description: String? = null,
    val textNumber: Int? = null
)

sealed class BookImageSource {
    data class Url(val url: String) : BookImageSource()
    data class Bytes(val bytes: String) : BookImageSource()
}

enum class ImageType {
    COVER,
    CARD,
    PICTURE,
    FRESCO,
    DIAGRAM
}



