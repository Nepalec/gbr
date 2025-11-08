package com.gbr.tabreading.di

import com.gbr.tabreading.navigation.ReadingFeature
import com.gbr.tabreading.navigation.ReadingFeatureImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReadingModule {

    @Binds
    @Singleton
    abstract fun bindReadingFeature(impl: ReadingFeatureImpl): ReadingFeature
}
