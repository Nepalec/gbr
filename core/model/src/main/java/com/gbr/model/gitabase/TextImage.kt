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
) {

    /**
     * Get file extension based on format
     */
    fun getExtension(): String {
        return when (format) {
            ImageFormat.GIF -> "gif"
            ImageFormat.PNG -> "png"
            ImageFormat.SVG -> "svg"
            ImageFormat.JPEG -> "jpg"
        }
    }

    /**
     * Get full file path with protocol
     */
    fun getFullPath(): String {
        return if (filePath.startsWith("/")) "file://$filePath" else "file:///$filePath"
    }

    /**
     * Get formatted description for HTML display
     */
    fun getFormattedDescription(): String {
        return description.replace("\"", "&quot;").replace("'", "&lsquo;")
    }

    /**
     * Get chapter and text number in format "chapter.text"
     */
    fun getNumber(): String {
        return "$chapterId.$textNumber"
    }

    /**
     * Check if this is a cover image
     */
    fun isCoverImage(): Boolean {
        return type == ImageType.BOOK_COVER || type == ImageType.SONG_COVER
    }

    /**
     * Check if this is a chapter number image
     */
    fun isChapterNumber(): Boolean {
        return type == ImageType.NUMBER
    }

    /**
     * Get display name for the image
     */
    fun getDisplayName(): String {
        return if (description.isNotBlank()) description else "Image $id"
    }

    /**
     * Check if image has content (not just a reference)
     */
    fun hasContent(): Boolean {
        return content.isNotBlank()
    }

    companion object {
        /**
         * Create a chapter number image
         */
        fun createChapterNumber(chapterId: Int): TextImage {
            return TextImage(
                id = "chapter_$chapterId",
                textId = "",
                chapterId = chapterId,
                textNumber = "",
                description = "Chapter $chapterId",
                type = ImageType.NUMBER,
                format = ImageFormat.PNG
            )
        }

        /**
         * Create a book cover image
         */
        fun createBookCover(
            id: String,
            description: String,
            fullPath: String
        ): TextImage {
            return TextImage(
                id = id,
                textId = "",
                chapterId = 0,
                textNumber = "",
                description = description,
                type = ImageType.BOOK_COVER,
                format = ImageFormat.JPEG,
                filePath = fullPath
            )
        }
    }
}

/**
 * Image type enumeration
 */
enum class ImageType(
    val value: Int,
    val description: String,
    val isStructural: Boolean = false
) {
    NUMBER(0, "Chapter Number", true),
    PICTURE(1, "Picture"),
    CARD(2, "Card"),
    DIAGRAM(3, "Diagram"),
    FRESCO(4, "Fresco"),
    PLACE(5, "Place"),
    BOOK_COVER(10, "Book Cover"),
    SONG_COVER(11, "Song Cover");

    /**
     * Check if this image type is structural (affects layout)
     */
    fun isStructuralType(): Boolean = isStructural
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
    SVG(4, "image/svg+xml", "svg");

    /**
     * Get file extension for this format
     */
    fun getExtension(): String = fileExtension
}
