package com.gbr.scrDownloader

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module for the scrDownloader feature.
 * This module provides dependencies specific to the downloader feature.
 */
@Module
@InstallIn(SingletonComponent::class)
object ScrDownloaderModule
