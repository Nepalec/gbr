package com.gbr.data.usecase

import android.content.Context
import com.gbr.common.strings.StringProvider
import com.gbr.data.R
import com.gbr.data.repository.GitabasesRepository
import com.gbr.datastore.datasource.GbrPreferencesDataSource
import com.gbr.model.gitabase.Gitabase
import com.gbr.model.gitabase.GitabaseID
import com.gbr.model.gitabase.GitabaseLang
import com.gbr.model.gitabase.GitabaseType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

/**
 * Use case for initializing Gitabase files on app launch.
 * Scans for existing gitabases, and if none found, extracts help gitabases from resources.
 * Also determines and sets the current gitabase based on user's language preference.
 */
class InitializeGitabasesUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scanGitabaseFilesUseCase: ScanGitabaseFilesUseCase,
    private val extractGitabasesUseCase: ExtractGitabasesUseCase,
    private val gitabasesRepository: GitabasesRepository,
    private val gbrPreferencesDataSource: GbrPreferencesDataSource,
    private val stringProvider: StringProvider
) {

    /**
     * Initializes Gitabase files by scanning and extracting if necessary.
     * Uses the default gitabases folder in external files directory.
     * Also determines and sets the current gitabase based on user preferences.
     *
     * @return Result containing the set of available Gitabases
     */
    suspend fun execute(): Result<Set<com.gbr.model.gitabase.Gitabase>> {
        val defaultFolder = File(context.getExternalFilesDir(null), "gitabases")
        return execute(defaultFolder.absolutePath)
    }

    /**
     * Initializes Gitabase files by scanning and extracting if necessary.
     * Also determines and sets the current gitabase based on user preferences.
     *
     * @param folderPath The folder to scan for gitabases
     * @return Result containing the set of available Gitabases
     */
    suspend fun execute(folderPath: String): Result<Set<com.gbr.model.gitabase.Gitabase>> {
        return withContext(Dispatchers.IO) {
            try {
                // First, scan for existing gitabases
                val scanResult = scanGitabaseFilesUseCase.execute(folderPath)

                if (scanResult.isSuccess) {
                    val gitabases = scanResult.getOrThrow()
                    if (gitabases.isNotEmpty()) {
                        // Found gitabases, set them in repository
                        gitabasesRepository.setAllGitabases(gitabases)

                        // Determine and set current gitabase
                        determineAndSetCurrentGitabase(gitabases)

                        return@withContext Result.success(gitabases)
                    }
                }

                // No gitabases found, extract help gitabases from resources
                val extractResult = extractHelpGitabases(folderPath)
                if (extractResult.isFailure) {
                    return@withContext Result.failure(
                        extractResult.exceptionOrNull()
                            ?: Exception(stringProvider.getString(R.string.error_failed_to_extract_help_gitabases))
                    )
                }

                // Scan again after extraction
                val rescanResult = scanGitabaseFilesUseCase.execute(folderPath)
                if (rescanResult.isSuccess) {
                    val gitabases = rescanResult.getOrThrow()
                    gitabasesRepository.setAllGitabases(gitabases)

                    // Determine and set current gitabase
                    determineAndSetCurrentGitabase(gitabases)

                    Result.success(gitabases)
                } else {
                    Result.failure(
                        rescanResult.exceptionOrNull()
                            ?: Exception(stringProvider.getString(R.string.error_failed_to_scan_after_extraction))
                    )
                }

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Extracts help gitabases (English and Russian) from resources.
     */
    private suspend fun extractHelpGitabases(folderPath: String): Result<List<String>> {
        return try {
            val destinationFolder = File(folderPath)
            if (!destinationFolder.exists()) {
                destinationFolder.mkdirs()
            }

            extractGitabasesUseCase.execute(
                destinationFolder = destinationFolder,
                resourceFiles = ExtractGitabasesUseCase.HELP_GITABASE_FILES
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Determines and sets the current gitabase based on user preferences and system language.
     * If no saved preference exists, defaults to help_rus for Russian users, help_eng for others.
     *
     * @param gitabases The set of available gitabases
     */
    private suspend fun determineAndSetCurrentGitabase(gitabases: Set<Gitabase>) {
        try {
            // Check if there's a saved preference
            val savedGitabaseId = gbrPreferencesDataSource.getLastUsedGitabase()

            if (savedGitabaseId != null) {
                // Try to find the saved gitabase
                val savedGitabase = gitabases.find { it.id == savedGitabaseId }
                if (savedGitabase != null) {
                    gitabasesRepository.setCurrentGitabase(savedGitabaseId)
                    return
                }
            }

            // No saved preference or saved gitabase not found, determine based on system language
            val systemLanguage = getSystemLanguage()
            val defaultGitabaseId = if (systemLanguage == "ru") {
                GitabaseID(GitabaseType.HELP, GitabaseLang.RUS)
            } else {
                GitabaseID(GitabaseType.HELP, GitabaseLang.ENG)
            }

            // Find the default gitabase
            val defaultGitabase = gitabases.find { it.id == defaultGitabaseId }
            if (defaultGitabase != null) {
                gitabasesRepository.setCurrentGitabase(defaultGitabaseId)
                // Save the default selection to datastore
                gbrPreferencesDataSource.setLastUsedGitabase(defaultGitabaseId)
            }
        } catch (e: Exception) {
            // If anything fails, just don't set a current gitabase
            // The app will still work, just without a pre-selected gitabase
        }
    }

    /**
     * Gets the system language code.
     *
     * @return The system language code (e.g., "ru", "en")
     */
    private fun getSystemLanguage(): String {
        val configuration = context.resources.configuration
        val locale = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            configuration.locale
        }
        return locale.language.lowercase()
    }
}
