package com.gbr.di

import com.gbr.navigation.DefaultNavigator
import com.gbr.scrDownloader.navigation.DownloaderFeature
import com.gbr.tabbooks.navigation.BooksFeature
import com.gbr.tabreading.navigation.ReadingFeature
import com.gbr.tabdiscuss.navigation.DiscussFeature
import com.gbr.tabnotes.navigation.NotesFeature
import com.gbr.tabprofile.navigation.ProfileFeature
import com.gbr.settings.navigation.SettingsFeature
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
    fun provideDefaultNavigator(
        booksFeature: BooksFeature,
        downloaderFeature: DownloaderFeature,
        readingFeature: ReadingFeature,
        discussFeature: DiscussFeature,
        notesFeature: NotesFeature,
        profileFeature: ProfileFeature,
        settingsFeature: SettingsFeature
    ): DefaultNavigator {
        return DefaultNavigator(
            booksFeature = booksFeature,
            downloaderFeature = downloaderFeature,
            readingFeature = readingFeature,
            discussFeature = discussFeature,
            notesFeature = notesFeature,
            profileFeature = profileFeature,
            settingsFeature = settingsFeature
        )
    }
}




