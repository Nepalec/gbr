package com.gbr.common.navigation

import kotlinx.serialization.Serializable

/**
 * Sub-graph destinations (feature modules)
 * These represent the main navigation areas in the app
 */
@Serializable
sealed class SubGraphDest {
    @Serializable
    data object Books : SubGraphDest()

    @Serializable
    data object BooksDownload : SubGraphDest()

    @Serializable
    data object Reading : SubGraphDest()

    @Serializable
    data object Discuss : SubGraphDest()

    @Serializable
    data object Notes : SubGraphDest()

    @Serializable
    data object Profile : SubGraphDest()

    @Serializable
    data object Settings : SubGraphDest()
}

/**
 * Individual screen destinations within each feature
 */
@Serializable
sealed class Dest {
    // Books feature destinations
    @Serializable
    data object Books : Dest()

    @Serializable
    data object BookDetail : Dest()

    // Books Download feature destinations
    @Serializable
    data object BooksDownload : Dest()

    @Serializable
    data object BooksDownloadDetail : Dest()

    // Reading feature destinations
    @Serializable
    data object Reading : Dest()

    // Discuss feature destinations
    @Serializable
    data object Discuss : Dest()

    // Notes feature destinations
    @Serializable
    data object Notes : Dest()

    // Profile feature destinations
    @Serializable
    data object Profile : Dest()

    // Settings feature destinations
    @Serializable
    data object Settings : Dest()
}

/**
 * Fullscreen content destinations
 * These represent screens that should be displayed in fullscreen mode (hiding bottom navigation)
 */
@Serializable
sealed class FullscreenDest {
    @Serializable
    data object Root : FullscreenDest()

    @Serializable
    data class BookDetail(
        val gitabaseIdKey: String, // GitabaseID.key
        val bookId: Int // BookPreview.id
    ) : FullscreenDest()

    @Serializable
    data class ChapterDetail(
        val gitabaseIdKey: String, // GitabaseID.key
        val bookId: Int, // BookPreview.id
        val chapterNumber: Int // Chapter number
    ) : FullscreenDest()

    @Serializable
    data object Login : FullscreenDest()
}