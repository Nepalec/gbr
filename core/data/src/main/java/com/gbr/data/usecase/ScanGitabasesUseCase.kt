package com.gbr.data.usecase

import com.gbr.data.model.Gitabase
import com.gbr.data.repository.GitabaseFilesRepo
import com.gbr.model.gitabase.GitabaseID
import com.gbr.model.gitabase.GitabaseLang
import com.gbr.model.gitabase.GitabaseType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

/**
 * Use case for scanning folders and discovering Gitabase database files.
 * Handles file system operations, database validation, and type/language detection.
 */
class ScanGitabasesUseCase @Inject constructor(
    private val gitabaseFilesRepo: GitabaseFilesRepo
) {

    /**
     * Scans the specified folder for Gitabase database files.
     * Validates each found file and adds valid databases to the repository.
     *
     * @param folderPath The path to the folder to scan
     * @return Result containing the list of discovered Gitabases or an error
     */
    suspend fun execute(folderPath: String): Result<List<Gitabase>> {
        return try {
            val folder = File(folderPath)
            if (!folder.exists() || !folder.isDirectory) {
                return Result.failure(IllegalArgumentException("Invalid folder path: $folderPath"))
            }

            // Find all .db files in the folder
            val gitabaseFiles = findGitabaseFiles(folder)
            val validGitabases = mutableListOf<Gitabase>()

            // Process each file: validate and create Gitabase objects
            for (file in gitabaseFiles) {
                if (validateGitabaseFile(file)) {
                    val gitabase = createGitabaseFromFile(file)
                    validGitabases.add(gitabase)
                    gitabaseFilesRepo.addGitabase(gitabase)
                }
            }

            Result.success(validGitabases)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Finds all .db files in the specified folder.
     * This operation involves file system traversal which can be time-consuming.
     */
    private suspend fun findGitabaseFiles(folder: File): List<File> {
        return withContext(Dispatchers.IO) {
            folder.listFiles { file ->
                file.isFile && file.extension.lowercase() == "db"
            }?.toList() ?: emptyList()
        }
    }

    /**
     * Validates if a file is a valid Gitabase database.
     * Performs basic checks for file existence, readability, and size.
     * This could be extended to perform more sophisticated SQLite validation.
     */
    private suspend fun validateGitabaseFile(file: File): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Basic validation - check if file is a valid SQLite database
                // This would need more sophisticated validation
                file.exists() && file.canRead() && file.length() > 0
            } catch (e: Exception) {
                false
            }
        }
    }

    /**
     * Creates a Gitabase object from a file.
     * Extracts database type and language from the filename pattern.
     */
    private fun createGitabaseFromFile(file: File): Gitabase {
        val gitabaseId = GitabaseID(
            type = determineDatabaseType(file.nameWithoutExtension),
            lang = determineLanguage(file.nameWithoutExtension)
        )
        val name = file.nameWithoutExtension
        val path = file.absolutePath

        return Gitabase(
            id = gitabaseId,
            name = name,
            path = path,
            type = gitabaseId.type,
            language = gitabaseId.lang,
            isValid = true,
            lastModified = file.lastModified()
        )
    }

    /**
     * Determines the database type from the filename.
     * Parses the pattern: gitabase_{type}_{lang}.db
     * Falls back to creating a custom GitabaseType if the type is not recognized.
     */
    private fun determineDatabaseType(name: String): GitabaseType {
        val sTemp = name.lowercase()
        if (sTemp.contains("gitabase")) {
            val sTemp2 = sTemp.substring(sTemp.indexOf("gitabase") + 9)
            val ind = sTemp2.indexOf("_")
            val sDBType = if (ind == -1) "" else sTemp2.substring(0, ind)

            return when (sDBType) {
                "texts" -> GitabaseType.TEXTS
                "help" -> GitabaseType.HELP
                "my-books" -> GitabaseType.MY_BOOKS
                "shop" -> GitabaseType.SHOP
                else -> GitabaseType(sDBType)
            }
        }
        return GitabaseType.TEXTS
    }

    /**
     * Determines the database language from the filename.
     * Parses the pattern: gitabase_{type}_{lang}.db
     * Falls back to creating a custom GitabaseLang if the language is not recognized.
     */
    private fun determineLanguage(name: String): GitabaseLang {
        val sTemp = name.lowercase()
        if (sTemp.contains("gitabase")) {
            val sTemp2 = sTemp.substring(sTemp.indexOf("gitabase") + 9)
            val ind = sTemp2.indexOf("_")
            val ind2 = sTemp2.indexOf(".")
            val sDBLang = if (ind == -1 || ind2 == -1) "" else sTemp2.substring(ind + 1, ind2)

            return when (sDBLang) {
                "eng" -> GitabaseLang.ENG
                "rus" -> GitabaseLang.RUS
                else -> GitabaseLang(sDBLang)
            }
        }
        return GitabaseLang.ENG
    }
}
