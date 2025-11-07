package com.gbr.scrchapter.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ChapterModule {
    // Module for Chapter feature dependencies
    // Add @Provides or @Binds methods here if needed
}

