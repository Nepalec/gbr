package com.gbr.tabprofile.di

import com.gbr.tabprofile.navigation.ProfileFeature
import com.gbr.tabprofile.navigation.ProfileFeatureImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileModule {

    @Binds
    @Singleton
    abstract fun bindProfileFeature(impl: ProfileFeatureImpl): ProfileFeature
}
