package com.gbr.navigation

import com.gbr.common.navigation.FullscreenDest
import com.gbr.navigation.FullscreenContent
import com.gbr.scrDownloader.navigation.DownloaderFeature
import com.gbr.tabbooks.navigation.BooksFeature
import com.gbr.tabreading.navigation.ReadingFeature
import com.gbr.tabdiscuss.navigation.DiscussFeature
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
    val discussFeature: DiscussFeature,
    val notesFeature: NotesFeature,
    val profileFeature: ProfileFeature,
    val settingsFeature: SettingsFeature,
    val fullscreenContent: FullscreenContent,
    val onNavigateToFullscreen: (FullscreenDest) -> Unit = {}

)