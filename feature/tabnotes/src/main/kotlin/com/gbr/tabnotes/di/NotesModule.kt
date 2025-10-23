package com.gbr.tabnotes.di

import com.gbr.tabnotes.navigation.NotesFeature
import com.gbr.tabnotes.navigation.NotesFeatureImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotesModule {
    
    @Binds
    @Singleton
    abstract fun bindNotesFeature(impl: NotesFeatureImpl): NotesFeature
}
