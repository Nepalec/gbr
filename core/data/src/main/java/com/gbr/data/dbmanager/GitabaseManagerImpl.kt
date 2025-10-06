package com.gbr.data.dbmanager

import com.gbr.data.database.GitabaseDatabase
import com.gbr.model.gitabase.Gitabase
import com.gbr.model.gitabase.GitabaseID
import com.gbr.model.gitabase.GitabaseLang
import com.gbr.model.gitabase.GitabaseType

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of GitabaseManager with dynamic database discovery
 * Scans Gitabase folder for available databases
 */
@Singleton
class GitabaseManagerImpl @Inject constructor(
    private val context: Context,
    private val gitabaseFolderPath: String
) : GitabaseManager {

    // 🎯 Cache database instances by GitabaseID key
    private val databases = mutableMapOf<String, GitabaseDatabase>()

    // 🎯 Cache discovered databases
    private var cachedDatabases: List<Gitabase>? = null

    override suspend fun scanForDatabases(): List<Gitabase> = withContext(Dispatchers.IO) {
        val discoveredDatabases = mutableListOf<Gitabase>()
        val gitabaseFolder = File(gitabaseFolderPath)

        if (!gitabaseFolder.exists() || !gitabaseFolder.isDirectory) {
            return@withContext emptyList()
        }

        // 🎯 Scan for .db files
        gitabaseFolder.listFiles { file ->
            file.isFile && file.extension.lowercase() == "db"
        }?.forEach { dbFile ->
            val gitabase = parseDatabaseInfo(dbFile)
            if (gitabase != null) {
                discoveredDatabases.add(gitabase)
            }
        }

        // 🎯 Sort by type and language
        discoveredDatabases.sortedWith(compareBy({ it.id.type.value }, { it.id.lang.value }))
    }

    override suspend fun getAvailableDatabases(): List<Gitabase> {
        return cachedDatabases ?: refreshAvailableDatabases()
    }

    override suspend fun refreshAvailableDatabases(): List<Gitabase> {
        val discoveredDatabases = scanForDatabases()
        cachedDatabases = discoveredDatabases
        return discoveredDatabases
    }

    /**
     * Parse database file to extract type and language information
     */

    // core/data/src/main/java/com/gitabase/core/data/database/GitabaseManagerImpl.kt
    /**
     * Parse database file to extract type and language information
     * Based on TextsDatabaseManager.scanFolder logic
     */
    private fun parseDatabaseInfo(dbFile: File): Gitabase? {
        val sTemp = dbFile.absolutePath

        // 🎯 Check if path contains "gitabase" (equivalent to sTemp.contains("gitabase"))
        if (!sTemp.contains("gitabase")) {
            return null
        }

        // 🎯 Extract substring after "gitabase" (equivalent to sTemp.substring(sTemp.indexOf("gitabase") + 9))
        val sTemp2 = sTemp.substring(sTemp.indexOf("gitabase") + 9)

        // 🎯 Find first underscore (equivalent to sTemp2.indexOf("_"))
        val ind = sTemp2.indexOf("_")
        val sDBType = if (ind == -1) "" else sTemp2.substring(0, ind)

        // 🎯 Find first dot (equivalent to sTemp2.indexOf("."))
        val ind2 = sTemp2.indexOf(".")
        val sDBLang = if (ind2 == -1) "" else sTemp2.substring(ind + 1, ind2)

        // 🎯 Check if both type and language are not empty (equivalent to the if condition)
        if (sDBType.isEmpty() || sDBLang.isEmpty()) {
            return null
        }

        // 🎯 Create Gitabase object
        return Gitabase(
            id = GitabaseID(GitabaseType(sDBType), GitabaseLang(sDBLang)),
            version = 1,
            filePath = dbFile.absolutePath,
            isShopDatabase = false,
            hasTranslation = true,
            lastModified = dbFile.lastModified()
        )
    }

    override fun getDatabase(gitabaseId: GitabaseID): GitabaseDatabase {
        val key = gitabaseId.key

        return databases.getOrPut(key) {
            GitabaseDatabase.getDatabase(
                context = context,
            )
        }
    }

    override fun databaseExists(gitabaseId: GitabaseID): Boolean {
        val path = getDatabasePath(gitabaseId)
        return File(path).exists()
    }



    override fun closeAllDatabases() {
        databases.values.forEach { database ->
            if (database.isOpen) {
                database.close()
            }
        }
        databases.clear()
        cachedDatabases = null
    }

    override fun closeDatabase(gitabaseId: GitabaseID) {
        val key = gitabaseId.key
        databases[key]?.let { database ->
            if (database.isOpen) {
                database.close()
            }
            databases.remove(key)
        }
    }

    override fun getOpenDatabaseCount(): Int {
        return databases.count { it.value.isOpen }
    }

    override fun isDatabaseOpen(gitabaseId: GitabaseID): Boolean {
        val key = gitabaseId.key
        return databases[key]?.isOpen ?: false
    }
}
