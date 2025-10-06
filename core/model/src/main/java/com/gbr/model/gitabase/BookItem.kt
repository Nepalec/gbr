package com.gbr.model.gitabase


/**
 * Represents a book in the Gitabase system
 */
data class BookItem(
    val id: Int,
    val uid: String,
    val title: String,
    val author: String,
    val issue: String = "",
    val abbreviation: String = "",
    val code: String = "",
    val compareCode: String = "",

    // Book properties
    val levels: Int = 0,
    val isSimple: Boolean = true,
    val isSongBook: Boolean = false,
    val isComicsBook: Boolean = false,
    val hasSanskrit: Boolean = false,
    val hasColorStructure: Boolean = false,
    val hideChapterNumbers: Boolean = false,
    val hasImages: Boolean = false,

    // Size information
    val textSize: Int = 0,
    val commentSize: Int = 0,
    val totalSize: Int = 0,

    // Database information
    val databaseType: String = "",
    val databaseLanguage: String = "",

    // UI state (transient)
    val bookmark: String = "",
    val isDeleting: Boolean = false,

    // Cover image
    val coverImage: TextImage? = null
) {

    /**
     * Unique identifier for this book across all databases
     */
    val uniqueKey: String
        get() = "${databaseLanguage}_${databaseType}_$id"

    /**
     * Check if this is a preview book
     */
    fun isPreview(): Boolean {
        return issue == "PREVIEW_BOOK"
    }

    /**
     * Check if this book has a bookmark
     */
    fun hasBookmark(): Boolean {
        return bookmark.isNotBlank()
    }

    /**
     * Check if this is a letters book (where TextNo is not unique)
     */
    fun isLetters(): Boolean {
        return code in listOf("LTRS", "TLKS", "ПШП")
    }

    /**
     * Get display title with author
     */
    fun getDisplayTitle(): String {
        return if (author.isNotBlank()) "$title - $author" else title
    }

    /**
     * Get short title for UI display
     */
    fun getShortTitle(maxLength: Int = 50): String {
        return if (title.length <= maxLength) title else "${title.take(maxLength - 3)}..."
    }

    companion object {
        /**
         * Create a copy with updated bookmark
         */
        fun BookItem.withBookmark(bookmark: String): BookItem {
            return copy(bookmark = bookmark)
        }

        /**
         * Create a copy with updated deletion state
         */
        fun BookItem.withDeletionState(isDeleting: Boolean): BookItem {
            return copy(isDeleting = isDeleting)
        }
    }
}
