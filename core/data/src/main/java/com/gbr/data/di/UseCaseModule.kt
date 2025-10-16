package com.gbr.data.di

import android.content.Context
import com.gbr.data.repository.GitabasesRepository
import com.gbr.data.repository.GitabasesDescRepository
import com.gbr.data.usecase.CopyGitabaseUseCase
import com.gbr.data.usecase.ExtractGitabasesUseCase
import com.gbr.data.usecase.RemoveGitabaseUseCase
import com.gbr.data.usecase.ScanGitabaseFilesUseCase
import com.gbr.data.usecase.InitializeGitabasesUseCase
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
    fun provideScanGitabasesUseCase(
        gitabasesRepository: GitabasesRepository,
        gitabasesDescRepository: GitabasesDescRepository
    ): ScanGitabaseFilesUseCase {
        return ScanGitabaseFilesUseCase(gitabasesRepository, gitabasesDescRepository)
    }

    @Provides
    @Singleton
    fun provideExtractGitabasesUseCase(@ApplicationContext context: Context): ExtractGitabasesUseCase {
        return ExtractGitabasesUseCase(context)
    }

    @Provides
    @Singleton
    fun provideCopyGitabaseUseCase(
        @ApplicationContext context: Context,
        scanGitabaseFilesUseCase: ScanGitabaseFilesUseCase
    ): CopyGitabaseUseCase {
        return CopyGitabaseUseCase(context, scanGitabaseFilesUseCase)
    }

    @Provides
    @Singleton
    fun provideRemoveGitabaseUseCase(@ApplicationContext context: Context): RemoveGitabaseUseCase {
        return RemoveGitabaseUseCase(context)
    }

    @Provides
    @Singleton
    fun provideInitializeGitabasesUseCase(
        @ApplicationContext context: Context,
        scanGitabaseFilesUseCase: ScanGitabaseFilesUseCase,
        extractGitabasesUseCase: ExtractGitabasesUseCase,
        gitabasesRepository: GitabasesRepository
    ): InitializeGitabasesUseCase {
        return InitializeGitabasesUseCase(context, scanGitabaseFilesUseCase, extractGitabasesUseCase, gitabasesRepository)
    }
}
