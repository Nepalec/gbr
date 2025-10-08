package com.gbr.data.di

import android.content.Context
import com.gbr.data.repository.GitabaseFilesRepo
import com.gbr.data.usecase.ExtractGitabasesUseCase
import com.gbr.data.usecase.ScanGitabasesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Provides
    @Singleton
    fun provideExtractGitabasesUseCase(@ApplicationContext context: Context): ExtractGitabasesUseCase {
        return ExtractGitabasesUseCase(context)
    }
}
