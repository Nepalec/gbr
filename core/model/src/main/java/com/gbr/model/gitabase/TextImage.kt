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
    fun getFileExtension(): String {
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
    fun getFullPathWithProtocol(): String {
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

}

/**
 * Image type enumeration
 */
enum class ImageType(
    val value: Int,
) {
    PICTURE(1),
    CARD(2),
    DIAGRAM(3),
    FRESCO(4)
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
