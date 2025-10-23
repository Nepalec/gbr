package com.gbr.navigation

/**
 * Top-level destinations in the app.
 * These represent the main tabs in the bottom navigation.
 */
enum class TopLevelDestination(
    val title: String,
    val icon: Int,
    val selectedIcon: Int
) {
    BOOKS(
        title = "Books",
        icon = com.gbr.designsystem.R.drawable.library_books_24px,
        selectedIcon = com.gbr.designsystem.R.drawable.library_books_filled_24px
    ),
    READING(
        title = "Reading",
        icon = com.gbr.designsystem.R.drawable.bookmark_24px,
        selectedIcon = com.gbr.designsystem.R.drawable.bookmark_filled_24px
    ),
    DISCUSS(
        title = "Discuss",
        icon = com.gbr.designsystem.R.drawable.forum_24px,
        selectedIcon = com.gbr.designsystem.R.drawable.chat_24px
    ),
    NOTES(
        title = "Notes",
        icon = com.gbr.designsystem.R.drawable.note_stack_24px,
        selectedIcon = com.gbr.designsystem.R.drawable.note_stack_filled_24px
    ),
    PROFILE(
        title = "Profile",
        icon = com.gbr.designsystem.R.drawable.person_24px,
        selectedIcon = com.gbr.designsystem.R.drawable.person_filled_24px
    )
}
