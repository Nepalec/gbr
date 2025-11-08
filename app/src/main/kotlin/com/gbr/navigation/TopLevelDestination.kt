package com.gbr.navigation

import com.gbr.R

/**
 * Top-level destinations in the app.
 * These represent the main tabs in the bottom navigation.
 */
enum class TopLevelDestination(
    val titleResId: Int,
    val icon: Int,
    val selectedIcon: Int
) {
    BOOKS(
        titleResId = R.string.nav_books,
        icon = com.gbr.designsystem.R.drawable.library_books_24px,
        selectedIcon = com.gbr.designsystem.R.drawable.library_books_filled_24px
    ),
    READING(
        titleResId = R.string.nav_reading,
        icon = com.gbr.designsystem.R.drawable.bookmark_24px,
        selectedIcon = com.gbr.designsystem.R.drawable.bookmark_filled_24px
    ),
    DISCUSS(
        titleResId = R.string.nav_discuss,
        icon = com.gbr.designsystem.R.drawable.forum_24px,
        selectedIcon = com.gbr.designsystem.R.drawable.chat_24px
    ),
    NOTES(
        titleResId = R.string.nav_notes,
        icon = com.gbr.designsystem.R.drawable.note_stack_24px,
        selectedIcon = com.gbr.designsystem.R.drawable.note_stack_filled_24px
    ),
    PROFILE(
        titleResId = R.string.nav_profile,
        icon = com.gbr.designsystem.R.drawable.person_24px,
        selectedIcon = com.gbr.designsystem.R.drawable.person_filled_24px
    )
}
