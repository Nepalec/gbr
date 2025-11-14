package com.gbr.di

import com.gbr.navigation.DefaultNavigator
import com.gbr.navigation.FullscreenContent
import com.gbr.scrDownloader.navigation.DownloaderFeature
import com.gbr.settings.navigation.SettingsFeature
import com.gbr.tabbooks.navigation.BooksFeature
import com.gbr.tabdiscuss.navigation.DiscussFeature
import com.gbr.tabnotes.navigation.NotesFeature
import com.gbr.tabprofile.navigation.ProfileFeature
import com.gbr.tabreading.navigation.ReadingFeature
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @Suppress("LongParameterList")
    fun provideDefaultNavigator(
        booksFeature: BooksFeature,
        downloaderFeature: DownloaderFeature,
        readingFeature: ReadingFeature,
        discussFeature: DiscussFeature,
        notesFeature: NotesFeature,
        profileFeature: ProfileFeature,
        settingsFeature: SettingsFeature,
        fullscreenContent: FullscreenContent
    ): DefaultNavigator {
        return DefaultNavigator(
            booksFeature = booksFeature,
            downloaderFeature = downloaderFeature,
            readingFeature = readingFeature,
            discussFeature = discussFeature,
            notesFeature = notesFeature,
            profileFeature = profileFeature,
            settingsFeature = settingsFeature,
            fullscreenContent = fullscreenContent
        )
    }
}