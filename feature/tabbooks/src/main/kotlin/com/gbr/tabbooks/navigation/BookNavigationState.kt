package com.gbr.tabbooks.navigation

import com.gbr.model.book.BookPreview
import com.gbr.model.gitabase.GitabaseID

/**
 * Simple global state for passing book navigation arguments
 * This is a lightweight solution that doesn't require Hilt annotations
 */
object BookNavigationState {
    private var _selectedBook: BookNavigationArgs? = null

    fun setSelectedBook(gitabaseId: GitabaseID, bookPreview: BookPreview) {
        _selectedBook = BookNavigationArgs(gitabaseId, bookPreview)
    }

    fun getSelectedBook(): BookNavigationArgs? = _selectedBook

    fun clearSelectedBook() {
        _selectedBook = null
    }
}

data class BookNavigationArgs(
    val gitabaseId: GitabaseID,
    val bookPreview: BookPreview
)
