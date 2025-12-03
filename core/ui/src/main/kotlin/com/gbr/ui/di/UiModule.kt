package com.gbr.ui.di

import com.gbr.ui.SnackbarHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UiModule {

    @Provides
    @Singleton
    fun provideSnackbarHelper(): SnackbarHelper = SnackbarHelper()
}

