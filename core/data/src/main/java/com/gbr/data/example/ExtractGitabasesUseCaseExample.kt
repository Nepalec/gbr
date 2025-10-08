package com.gbr.data.example

import com.gbr.data.usecase.ExtractGitabasesUseCase
import java.io.File
import javax.inject.Inject

/**
 * Example of how to use ExtractGitabasesUseCase.
 * This class demonstrates the usage patterns for extracting Gitabase files from resources.
 */
class ExtractGitabasesUseCaseExample @Inject constructor(
    private val extractGitabasesUseCase: ExtractGitabasesUseCase
) {

    /**
     * Example: Extract default Gitabase files to a folder
     */
    suspend fun extractDefaultGitabases(destinationFolder: File) {
        val result = extractGitabasesUseCase.execute(destinationFolder)
        
        if (result.isSuccess) {
            val extractedFiles = result.getOrThrow()
            println("✅ Extracted ${extractedFiles.size} Gitabase files:")
            extractedFiles.forEach { filePath ->
                println("  - $filePath")
            }
        } else {
            val error = result.exceptionOrNull()
            println("❌ Failed to extract Gitabase files: ${error?.message}")
        }
    }

    /**
     * Example: Extract specific Gitabase files
     */
    suspend fun extractSpecificGitabases(destinationFolder: File) {
        val specificFiles = listOf(
            "gitabase_help_eng.db",
            "gitabase_help_rus.db"
        )
        
        val result = extractGitabasesUseCase.execute(destinationFolder, specificFiles)
        
        if (result.isSuccess) {
            val extractedFiles = result.getOrThrow()
            println("✅ Extracted specific Gitabase files:")
            extractedFiles.forEach { filePath ->
                println("  - $filePath")
            }
        } else {
            val error = result.exceptionOrNull()
            println("❌ Failed to extract specific Gitabase files: ${error?.message}")
        }
    }

    /**
     * Example: Check available Gitabase files in resources
     */
    fun checkAvailableGitabaseFiles() {
        val availableFiles = extractGitabasesUseCase.getAvailableGitabaseFiles()
        println("📁 Available Gitabase files in resources:")
        availableFiles.forEach { fileName ->
            println("  - $fileName")
        }
    }

    /**
     * Example: Check if a specific file is available
     */
    fun checkSpecificFileAvailability() {
        val fileName = "gitabase_help_eng.db"
        val isAvailable = extractGitabasesUseCase.isGitabaseFileAvailable(fileName)
        
        if (isAvailable) {
            println("✅ $fileName is available in resources")
        } else {
            println("❌ $fileName is not available in resources")
        }
    }

    /**
     * Example: Extract to app's external files directory
     */
    suspend fun extractToExternalFiles(context: android.content.Context) {
        val externalFilesDir = context.getExternalFilesDir(null)
        val gitabaseFolder = File(externalFilesDir, "gitabases")
        
        val result = extractGitabasesUseCase.execute(gitabaseFolder)
        
        if (result.isSuccess) {
            println("✅ Gitabase files extracted to: ${gitabaseFolder.absolutePath}")
        } else {
            println("❌ Failed to extract to external files: ${result.exceptionOrNull()?.message}")
        }
    }
}
