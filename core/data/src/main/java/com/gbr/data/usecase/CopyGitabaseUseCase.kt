package com.gbr.data.usecase

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject

/**
 * Use case for copying a Gitabase file from local storage to the app's gitabase folder.
 * Validates the source file and copies it to the destination folder.
 */
class CopyGitabaseUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val scanGitabaseFilesUseCase: ScanGitabaseFilesUseCase
) {

    /**
     * Copies a Gitabase file from the source path to the app's gitabase folder.
     * 
     * @param sourceFilePath The path to the source .db file
     * @return Result containing the destination file path or an error
     */
    suspend fun execute(sourceFilePath: String): Result<String> {
        return try {
            val sourceFile = File(sourceFilePath)
            
            // Validate source file basic properties
            if (!sourceFile.exists()) {
                return Result.failure(IllegalArgumentException("Source file does not exist: $sourceFilePath"))
            }
            
            if (!sourceFile.isFile) {
                return Result.failure(IllegalArgumentException("Source path is not a file: $sourceFilePath"))
            }
            
            if (!sourceFile.canRead()) {
                return Result.failure(IllegalArgumentException("Cannot read source file: $sourceFilePath"))
            }
            
            if (!sourceFilePath.lowercase().endsWith(".db")) {
                return Result.failure(IllegalArgumentException("Source file must be a .db file: $sourceFilePath"))
            }
            
            // Validate that the source file is a valid gitabase
            val validationResult = validateGitabaseFile(sourceFile)
            if (validationResult.isFailure) {
                return Result.failure(IllegalArgumentException("Source file is not a valid Gitabase: ${validationResult.exceptionOrNull()?.message}"))
            }
            
            // Create destination folder if it doesn't exist
            val gitabasesFolder = File(context.getExternalFilesDir(null), "gitabases")
            if (!gitabasesFolder.exists()) {
                gitabasesFolder.mkdirs()
            }
            
            // Create destination file with the same name as source
            val destinationFile = File(gitabasesFolder, sourceFile.name)
            
            // Copy the file
            copyFile(sourceFile, destinationFile)
            
            Result.success(destinationFile.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Validates that a file is a valid Gitabase by attempting to scan it.
     */
    private suspend fun validateGitabaseFile(sourceFile: File): Result<Unit> {
        return try {
            // Create a temporary folder to scan the source file
            val tempFolder = File(context.cacheDir, "temp_validation")
            if (!tempFolder.exists()) {
                tempFolder.mkdirs()
            }
            
            // Copy the source file to temp folder for validation
            val tempFile = File(tempFolder, sourceFile.name)
            copyFile(sourceFile, tempFile)
            
            // Try to scan the temp folder to validate the gitabase
            val scanResult = scanGitabaseFilesUseCase.execute(tempFolder.absolutePath)
            
            // Clean up temp folder
            tempFile.delete()
            if (tempFolder.listFiles()?.isEmpty() == true) {
                tempFolder.delete()
            }
            
            if (scanResult.isSuccess) {
                val gitabases = scanResult.getOrThrow()
                if (gitabases.any { it.filePath == tempFile.absolutePath }) {
                    Result.success(Unit)
                } else {
                    Result.failure(IllegalArgumentException("File is not recognized as a valid Gitabase"))
                }
            } else {
                Result.failure(IllegalArgumentException("Failed to validate Gitabase file: ${scanResult.exceptionOrNull()?.message}"))
            }
        } catch (e: Exception) {
            Result.failure(IllegalArgumentException("Error validating Gitabase file: ${e.message}"))
        }
    }
    
    /**
     * Copies a file from source to destination.
     */
    private fun copyFile(source: File, destination: File) {
        FileInputStream(source).use { input ->
            FileOutputStream(destination).use { output ->
                input.copyTo(output)
            }
        }
    }
}
