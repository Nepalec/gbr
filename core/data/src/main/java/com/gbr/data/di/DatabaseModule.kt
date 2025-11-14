package com.gbr.data.di

import android.content.Context
import com.gbr.common.strings.StringProvider
import com.gbr.data.R
import com.gbr.data.database.GitabaseDatabaseManager
import com.gbr.data.repository.GitabasesRepository
import com.gbr.data.repository.TextsRepository
import com.gbr.data.repository.TextsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger module for providing database-related dependencies.
 * Configures the database connection manager and repository implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provides the GitabaseDatabaseManager with gitabase folder path configuration.
     * Uses the external files directory for gitabase storage.
     */
    @Provides
    @Singleton
    fun provideGitabaseDatabaseManager(
        @ApplicationContext context: Context,
        stringProvider: StringProvider
    ): GitabaseDatabaseManager {
        val gitabaseFolderPath = context.getExternalFilesDir(null)?.let {
            "${it.absolutePath}/gitabases"
        }
            ?: throw IllegalStateException(
                stringProvider.getString(R.string.error_external_files_directory_not_available)
            )

        return GitabaseDatabaseManager(
            context = context,
            gitabaseFolderPath = gitabaseFolderPath,
            maxCacheSize = 3 // Keep up to 3 databases open for optimal performance
        )
    }

    /**
     * Provides the TextsRepository implementation.
     * Uses the database manager for cached database access.
     */
    @Provides
    @Singleton
    fun provideTextsRepository(
        @ApplicationContext context: Context,
        gitabasesRepository: GitabasesRepository,
        databaseManager: GitabaseDatabaseManager,
        stringProvider: StringProvider
    ): TextsRepository {
        return TextsRepositoryImpl(
            context = context,
            gitabasesRepository = gitabasesRepository,
            databaseManager = databaseManager,
            stringProvider = stringProvider
        )
    }
}