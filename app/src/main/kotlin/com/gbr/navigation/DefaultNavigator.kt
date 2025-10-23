package com.gbr.navigation

import com.gbr.common.network.Feature
import com.gbr.scrDownloader.navigation.DownloaderFeature
import com.gbr.tabbooks.navigation.BooksFeature
import com.gbr.tabreading.navigation.ReadingFeature
import com.gbr.tabnotes.navigation.NotesFeature
import com.gbr.tabprofile.navigation.ProfileFeature
import com.gbr.settings.navigation.SettingsFeature

/**
 * Navigation coordinator that holds all feature implementations
 */
data class DefaultNavigator(
    val booksFeature: BooksFeature,
    val downloaderFeature: DownloaderFeature,
    val readingFeature: ReadingFeature,
    val notesFeature: NotesFeature,
    val profileFeature: ProfileFeature,
    val settingsFeature: SettingsFeature
) {
    /**
     * Get all features as a list for easy iteration
     */
    fun getAllFeatures(): List<Feature> = listOf(
        booksFeature,
        downloaderFeature,
        readingFeature,
        notesFeature,
        profileFeature,
        settingsFeature
    )
}
