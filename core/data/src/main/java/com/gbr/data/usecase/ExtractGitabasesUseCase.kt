package com.gbr.data.usecase

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

/**
 * Use case for extracting Gitabase database files from app resources to device storage.
 * This allows the app to provide default Gitabase files bundled with the app.
 */
class ExtractGitabasesUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Extracts Gitabase database files from app resources to the specified destination folder.
     * 
     * @param destinationFolder The folder where the Gitabase files should be extracted
     * @param resourceFiles List of resource file names to extract (defaults to common Gitabase files)
     * @return Result containing the list of extracted file paths or an error
     */
    suspend fun execute(
        destinationFolder: File,
        resourceFiles: List<String> = DEFAULT_GITABASE_FILES
    ): Result<List<String>> {
        return try {
            // Ensure destination folder exists
            if (!destinationFolder.exists()) {
                destinationFolder.mkdirs()
            }

            if (!destinationFolder.isDirectory) {
                return Result.failure(IllegalArgumentException("Destination must be a directory: ${destinationFolder.absolutePath}"))
            }

            val extractedFiles = mutableListOf<String>()

            resourceFiles.forEach { fileName ->
                val extractedFile = extractSingleFile(fileName, destinationFolder)
                extractedFiles.add(extractedFile.absolutePath)
            }

            Result.success(extractedFiles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Extracts a single Gitabase file from resources to the destination folder.
     * 
     * @param fileName The name of the file in resources
     * @param destinationFolder The destination folder
     * @return The extracted file
     * @throws RuntimeException if the resource is not found or extraction fails
     */
    private fun extractSingleFile(fileName: String, destinationFolder: File): File {
        try {
            // Read from resources
            val resourceStream = context.resources.assets.open("gitabases/$fileName")
            
            // Create destination file
            val destinationFile = File(destinationFolder, fileName)
            
            // Copy to device storage
            destinationFile.outputStream().use { output ->
                resourceStream.use { input ->
                    input.copyTo(output)
                }
            }

            return destinationFile
        } catch (e: Exception) {
            throw RuntimeException("Failed to extract Gitabase file '$fileName': ${e.message}", e)
        }
    }

    /**
     * Gets the list of available Gitabase files in the app resources.
     * 
     * @return List of available Gitabase file names
     */
    fun getAvailableGitabaseFiles(): List<String> {
        return try {
            context.resources.assets.list("gitabases")?.toList() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Checks if a specific Gitabase file exists in resources.
     * 
     * @param fileName The name of the file to check
     * @return True if the file exists in resources, false otherwise
     */
    fun isGitabaseFileAvailable(fileName: String): Boolean {
        return try {
            context.resources.assets.open("gitabases/$fileName").use { true }
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        /**
         * Default list of Gitabase files to extract.
         */
        val DEFAULT_GITABASE_FILES = listOf(
            "gitabase_help_eng.db",
            "gitabase_help_rus.db",
            "gitabase_songs_rus.db"
        )
    }
}
