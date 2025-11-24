package com.gbr.model.gitabase

/**
 * Represents an image associated with text content in the Gitabase system
 */
data class TextImage(
    val id: String,
    val textId: String,
    val chapterId: Int,
    val textNumber: String,
    val description: String = "",
    val content: String = "",
    val type: ImageType,
    val format: ImageFormat,
    val filePath: String = "",
    val sortIndex: Int = 0,
    val randomIndex: Int = 0
)

/**
 * Image type enumeration
 */
enum class ImageType(
    val value: Int
) {
    PICTURE(1),
    CARD(2),
    DIAGRAM(3),
    FRESCO(4);


    companion object {
        private val map = entries.associateBy(ImageType::value)

        fun fromValue(value: Int): ImageType =
            map[value] ?: error("Unknown ImageType value: $value")
    }
}

/**
 * Image format enumeration
 */
enum class ImageFormat(
    val value: Int,
    val mimeType: String,
    val fileExtension: String
) {
    GIF(1, "image/gif", "gif"),
    PNG(2, "image/png", "png"),
    JPEG(3, "image/jpeg", "jpg"),
    SVG(4, "image/svg+xml", "svg")
}