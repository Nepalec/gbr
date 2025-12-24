package com.gbr.data.di

import android.content.Context
import com.gbr.common.strings.StringProvider
import com.gbr.data.repository.GitabasesDescRepository
import com.gbr.data.repository.GitabasesRepository
import com.gbr.data.repository.ImageFilesRepository
import com.gbr.data.repository.TextsRepository
import com.gbr.data.repository.UserPreferencesRepository
import com.gbr.data.repository.UserPreferencesRepositoryImpl
import com.gbr.data.repository.FileRepository
import com.gbr.data.usecase.CopyGitabaseUseCase
import com.gbr.data.usecase.DownloadAndUnzipUseCase
import com.gbr.data.usecase.ExtractGitabasesUseCase
import com.gbr.data.usecase.ImportNotesFromSqliteUseCase
import com.gbr.data.usecase.ImportSqliteNotesUseCase
import com.gbr.data.usecase.InitializeGitabasesUseCase
import com.gbr.data.usecase.LoadBookDetailUseCase
import com.gbr.data.usecase.RemoveGitabaseUseCase
import com.gbr.data.usecase.ScanGitabaseFilesUseCase
import com.gbr.data.usecase.SetCurrentGitabaseUseCase
import com.gbr.data.auth.AuthRepository
import com.gbr.data.repository.SqliteNotesRepositoryImpl
import com.gbr.data.repository.UserNotesRepository
import com.gbr.network.INotesBackupImportDataSource
import com.gbr.datastore.datasource.GbrPreferencesDataSource
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
        gitabasesDescRepository: GitabasesDescRepository,
        stringProvider: StringProvider
    ): ScanGitabaseFilesUseCase {
        return ScanGitabaseFilesUseCase(gitabasesRepository, gitabasesDescRepository, stringProvider)
    }

    @Provides
    @Singleton
    fun provideExtractGitabasesUseCase(
        @ApplicationContext context: Context,
        stringProvider: StringProvider
    ): ExtractGitabasesUseCase {
        return ExtractGitabasesUseCase(context, stringProvider)
    }

    @Provides
    @Singleton
    fun provideCopyGitabaseUseCase(
        @ApplicationContext context: Context,
        scanGitabaseFilesUseCase: ScanGitabaseFilesUseCase,
        stringProvider: StringProvider
    ): CopyGitabaseUseCase {
        return CopyGitabaseUseCase(context, scanGitabaseFilesUseCase, stringProvider)
    }

    @Provides
    @Singleton
    fun provideRemoveGitabaseUseCase(
        @ApplicationContext context: Context,
        stringProvider: StringProvider
    ): RemoveGitabaseUseCase {
        return RemoveGitabaseUseCase(context, stringProvider)
    }

    @Provides
    @Singleton
    fun provideInitializeGitabasesUseCase(
        @ApplicationContext context: Context,
        scanGitabaseFilesUseCase: ScanGitabaseFilesUseCase,
        extractGitabasesUseCase: ExtractGitabasesUseCase,
        gitabasesRepository: GitabasesRepository,
        gbrPreferencesDataSource: GbrPreferencesDataSource,
        stringProvider: StringProvider
    ): InitializeGitabasesUseCase {
        return InitializeGitabasesUseCase(
            context,
            scanGitabaseFilesUseCase,
            extractGitabasesUseCase,
            gitabasesRepository,
            gbrPreferencesDataSource,
            stringProvider
        )
    }

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(
        gbrPreferencesDataSource: GbrPreferencesDataSource
    ): UserPreferencesRepository {
        return UserPreferencesRepositoryImpl(gbrPreferencesDataSource)
    }

    @Provides
    @Singleton
    fun provideSetCurrentGitabaseUseCase(
        gitabasesRepository: GitabasesRepository,
        userPreferencesRepository: UserPreferencesRepository
    ): SetCurrentGitabaseUseCase {
        return SetCurrentGitabaseUseCase(gitabasesRepository, userPreferencesRepository)
    }

    @Provides
    @Singleton
    fun provideLoadBookDetailUseCase(
        textsRepository: TextsRepository,
        imageFilesRepository: ImageFilesRepository,
        stringProvider: StringProvider
    ): LoadBookDetailUseCase {
        return LoadBookDetailUseCase(textsRepository, imageFilesRepository, stringProvider)
    }

    @Provides
    @Singleton
    fun provideDownloadAndUnzipUseCase(
        fileRepository: FileRepository
    ): DownloadAndUnzipUseCase {
        return DownloadAndUnzipUseCase(fileRepository)
    }

    @Provides
    @Singleton
    fun provideImportSqliteNotesUseCase(
        notesBackupImportDataSource: INotesBackupImportDataSource,
        userNotesRepository: UserNotesRepository,
        authRepository: AuthRepository,
        userPreferencesRepository: UserPreferencesRepository
    ): ImportSqliteNotesUseCase {
        return ImportSqliteNotesUseCase(notesBackupImportDataSource, userNotesRepository, authRepository, userPreferencesRepository)
    }

    @Provides
    @Singleton
    fun provideImportNotesFromSqliteUseCase(
        userNotesRepository: UserNotesRepository,
        sqliteNotesRepositoryImpl: SqliteNotesRepositoryImpl
    ): ImportNotesFromSqliteUseCase {
        return ImportNotesFromSqliteUseCase(userNotesRepository, sqliteNotesRepositoryImpl)
    }
}