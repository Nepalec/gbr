package com.gbr.navigation.di

import com.gbr.navigation.FullscreenContent
import com.gbr.navigation.FullscreenContentImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NavigationModule {

    @Binds
    @Singleton
    abstract fun bindFullscreenContent(impl: FullscreenContentImpl): FullscreenContent
}

