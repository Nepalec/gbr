package com.gbr.settings.di

import com.gbr.settings.navigation.SettingsFeature
import com.gbr.settings.navigation.SettingsFeatureImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {
    
    @Binds
    @Singleton
    abstract fun bindSettingsFeature(impl: SettingsFeatureImpl): SettingsFeature
}
