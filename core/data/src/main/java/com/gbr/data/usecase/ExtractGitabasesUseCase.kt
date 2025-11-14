package com.gbr.data.usecase

import android.content.Context
import com.gbr.common.strings.StringProvider
import com.gbr.data.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

/**
 * Use case for extracting Gitabase database files from app resources to device storage.
 * This allows the app to provide default Gitabase files bundled with the app.
 */
class ExtractGitabasesUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val stringProvider: StringProvider
) {

    /**
     * Extracts Gitabase database files from app resources to the specified destination folder.
     * Extracts 4 files: 2 help gitabases and 2 test gitabases.
     *
     * @param destinationFolder The folder where the Gitabase files should be extracted
     * @param resourceFiles List of resource file names to extract (defaults to all 4 files)
     * @return Result containing the list of extracted file paths or an error
     */
    suspend fun execute(
        destinationFolder: File,
        resourceFiles: List<String> = HELP_GITABASE_FILES
    ): Result<List<String>> {
        return try {
            // Ensure destination folder exists
            if (!destinationFolder.exists()) {
                destinationFolder.mkdirs()
            }

            if (!destinationFolder.isDirectory) {
                return Result.failure(
                    IllegalArgumentException(
                        stringProvider.getString(
                            R.string.error_destination_must_be_directory,
                            destinationFolder.absolutePath
                        )
                    )
                )
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
     * Handles both help gitabases (from gitabases/ folder) and test gitabases (from test_gitabases/ folder).
     *
     * @param fileName The name of the file in resources
     * @param destinationFolder The destination folder
     * @return The extracted file
     * @throws RuntimeException if the resource is not found or extraction fails
     */
    private fun extractSingleFile(fileName: String, destinationFolder: File): File {
        try {
            // Determine the resource path based on file type
            val resourcePath = when {
                fileName in HELP_GITABASE_FILES -> "gitabases/$fileName"
                fileName in TEST_GITABASE_FILES -> "test_gitabases/$fileName"
                else -> "gitabases/$fileName" // Default fallback
            }

            // Read from resources
            val resourceStream = context.resources.assets.open(resourcePath)

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
            val msg = stringProvider.getString(
                R.string.error_failed_to_extract_gitabase_file,
                fileName,
                e.message ?: ""
            )
            throw RuntimeException(
                msg,
                e
            )
        }
    }

    /**
     * Gets the list of available Gitabase files in the app resources.
     * Returns files from both gitabases/ and test_gitabases/ folders.
     *
     * @return List of available Gitabase file names
     */
    fun getAvailableGitabaseFiles(): List<String> {
        return try {
            val helpFiles = context.resources.assets.list("gitabases")?.toList() ?: emptyList()
            val testFiles = context.resources.assets.list("test_gitabases")?.toList() ?: emptyList()
            helpFiles + testFiles
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Checks if a specific Gitabase file exists in resources.
     * Checks both gitabases/ and test_gitabases/ folders.
     *
     * @param fileName The name of the file to check
     * @return True if the file exists in resources, false otherwise
     */
    fun isGitabaseFileAvailable(fileName: String): Boolean {
        return try {
            when {
                fileName in HELP_GITABASE_FILES -> {
                    context.resources.assets.open("gitabases/$fileName").use { true }
                }

                fileName in TEST_GITABASE_FILES -> {
                    context.resources.assets.open("test_gitabases/$fileName").use { true }
                }

                else -> {
                    // Try both locations
                    try {
                        context.resources.assets.open("gitabases/$fileName").use { true }
                    } catch (e: Exception) {
                        try {
                            context.resources.assets.open("test_gitabases/$fileName").use { true }
                        } catch (e: Exception) {
                            false
                        }
                    }
                }
            }
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        /**
         * Help Gitabase files (2 files) - located in gitabases/ folder
         */
        val HELP_GITABASE_FILES = listOf(
            "gitabase_help_eng.db",
            "gitabase_help_rus.db"
        )

        /**
         * Test Gitabase files (2 files) - located in test_gitabases/ folder
         */
        val TEST_GITABASE_FILES = listOf(
            "gitabase_songs_rus.db",
            "gitabase_invaliddb_eng.db"
        )

        /**
         * All Gitabase files (4 files total: 2 help + 2 test)
         */
        val ALL_GITABASE_FILES = HELP_GITABASE_FILES + TEST_GITABASE_FILES
    }
}