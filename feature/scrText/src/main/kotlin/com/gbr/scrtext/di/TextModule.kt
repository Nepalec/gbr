package com.gbr.scrtext.di

import com.gbr.scrtext.screen.DefaultTextHtmlColorProvider
import com.gbr.scrtext.screen.TextHtmlColorProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TextModule {
    @Provides
    @Singleton
    fun provideTextHtmlColorProvider(): TextHtmlColorProvider {
        return DefaultTextHtmlColorProvider()
    }
}
