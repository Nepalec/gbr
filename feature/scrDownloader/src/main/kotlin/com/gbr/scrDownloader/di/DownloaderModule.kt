package com.gbr.scrDownloader.di

import com.gbr.scrDownloader.navigation.DownloaderFeature
import com.gbr.scrDownloader.navigation.DownloaderFeatureImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DownloaderModule {

    @Binds
    abstract fun bindDownloaderFeature(
        downloaderFeatureImpl: DownloaderFeatureImpl
    ): DownloaderFeature
}
