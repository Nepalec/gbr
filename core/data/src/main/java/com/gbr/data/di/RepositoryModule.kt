package com.gbr.data.di

import com.gbr.data.repository.GitabasesDescRepository
import com.gbr.data.repository.GitabasesDescRepositoryImpl
import com.gbr.data.repository.GitabasesRepository
import com.gbr.data.repository.GitabasesRepositoryImpl
import com.gbr.data.repository.ImageFilesRepository
import com.gbr.data.repository.ImageFilesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGitabasesRepository(gitabasesRepositoryImpl: GitabasesRepositoryImpl): GitabasesRepository

    @Binds
    @Singleton
    abstract fun bindGitabasesDescRepository(gitabasesDescRepositoryImpl: GitabasesDescRepositoryImpl): GitabasesDescRepository

    @Binds
    @Singleton
    abstract fun bindImageFilesRepository(imageFilesRepositoryImpl: ImageFilesRepositoryImpl): ImageFilesRepository
}
