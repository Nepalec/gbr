package com.gbr.data.usecase

import android.content.Context
import com.gbr.data.repository.GitabasesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

/**
 * Use case for initializing Gitabase files on app launch.
 * Scans for existing gitabases, and if none found, extracts help gitabases from resources.
 */
class InitializeGitabasesUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scanGitabaseFilesUseCase: ScanGitabaseFilesUseCase,
    private val extractGitabasesUseCase: ExtractGitabasesUseCase,
    private val gitabasesRepository: GitabasesRepository
) {

    /**
     * Initializes Gitabase files by scanning and extracting if necessary.
     * Uses the default gitabases folder in external files directory.
     * 
     * @return Result containing the list of available Gitabases
     */
    suspend fun execute(): Result<List<com.gbr.model.gitabase.Gitabase>> {
        val defaultFolder = File(context.getExternalFilesDir(null), "gitabases")
        return execute(defaultFolder.absolutePath)
    }

    /**
     * Initializes Gitabase files by scanning and extracting if necessary.
     * 
     * @param folderPath The folder to scan for gitabases
     * @return Result containing the list of available Gitabases
     */
    suspend fun execute(folderPath: String): Result<List<com.gbr.model.gitabase.Gitabase>> {
        return withContext(Dispatchers.IO) {
            try {
                // First, scan for existing gitabases
                val scanResult = scanGitabaseFilesUseCase.execute(folderPath)
                
                if (scanResult.isSuccess) {
                    val gitabases = scanResult.getOrThrow()
                    if (gitabases.isNotEmpty()) {
                        // Found gitabases, return them
                        return@withContext Result.success(gitabases)
                    }
                }
                
                // No gitabases found, extract help gitabases from resources
                val extractResult = extractHelpGitabases(folderPath)
                if (extractResult.isFailure) {
                    return@withContext Result.failure(extractResult.exceptionOrNull() ?: Exception("Failed to extract help gitabases"))
                }
                
                // Scan again after extraction
                val rescanResult = scanGitabaseFilesUseCase.execute(folderPath)
                if (rescanResult.isSuccess) {
                    Result.success(rescanResult.getOrThrow())
                } else {
                    Result.failure(rescanResult.exceptionOrNull() ?: Exception("Failed to scan after extraction"))
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
}
