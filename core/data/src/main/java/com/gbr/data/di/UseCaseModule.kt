package com.gbr.data.di

import com.gbr.data.repository.GitabaseFilesRepo
import com.gbr.data.usecase.ScanGitabasesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideScanGitabasesUseCase(gitabaseFilesRepo: GitabaseFilesRepo): ScanGitabasesUseCase {
        return ScanGitabasesUseCase(gitabaseFilesRepo)
    }
}
