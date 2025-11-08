package com.gbr.navigation

import com.gbr.common.navigation.FullscreenDest
import com.gbr.scrDownloader.navigation.DownloaderFeature
import com.gbr.settings.navigation.SettingsFeature
import com.gbr.tabbooks.navigation.BooksFeature
import com.gbr.tabdiscuss.navigation.DiscussFeature
import com.gbr.tabnotes.navigation.NotesFeature
import com.gbr.tabprofile.navigation.ProfileFeature
import com.gbr.tabreading.navigation.ReadingFeature

/**
 * Navigation coordinator that holds all feature implementations
 */
data class DefaultNavigator(
    val booksFeature: BooksFeature,
    val downloaderFeature: DownloaderFeature,
    val readingFeature: ReadingFeature,
    val discussFeature: DiscussFeature,
    val notesFeature: NotesFeature,
    val profileFeature: ProfileFeature,
    val settingsFeature: SettingsFeature,
    val fullscreenContent: FullscreenContent,
    val onNavigateToFullscreen: (FullscreenDest) -> Unit = {}

)