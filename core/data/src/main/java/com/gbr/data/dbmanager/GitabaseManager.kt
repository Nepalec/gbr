package com.gbr.data.dbmanager

import com.gbr.data.database.GitabaseDatabase
import com.gbr.model.gitabase.Gitabase
import com.gbr.model.gitabase.GitabaseID



/**
 * Interface for managing multiple Gitabase database instances
 * Handles database discovery, caching, and lifecycle management
 */
interface GitabaseManager {

    /**
     * Get database instance for specific GitabaseID
     * @param gitabaseId Unique identifier for the database
     * @return GitabaseDatabase instance for the specified database
     */
    fun getDatabase(gitabaseId: GitabaseID): GitabaseDatabase

    /**
     * Scan Gitabase folder for available databases
     * @return List of discovered database configurations
     */
    suspend fun scanForDatabases(): List<Gitabase>

    /**
     * Get all available databases (cached or scanned)
     * @return List of available database configurations
     */
    suspend fun getAvailableDatabases(): List<Gitabase>

    /**
     * Refresh database list by scanning folder again
     * @return Updated list of available databases
     */
    suspend fun refreshAvailableDatabases(): List<Gitabase>

    /**
     * Check if database exists for given GitabaseID
     * @param gitabaseId Unique identifier for the database
     * @return true if database file exists, false otherwise
     */
    fun databaseExists(gitabaseId: GitabaseID): Boolean

    /**
     * Get database path for given GitabaseID
     * @param gitabaseId Unique identifier for the database
     * @return Full path to database file
     */
    //fun getDatabasePath(gitabaseId: GitabaseID): String

    /**
     * Get database info for given GitabaseID
     * @param gitabaseId Unique identifier for the database
     * @return Gitabase for the specified database
     */
   // suspend fun getDatabaseInfo(gitabaseId: GitabaseID): Gitabase?

    /**
     * Close all database instances
     * Should be called when app is destroyed
     */
    fun closeAllDatabases()

    /**
     * Close specific database instance
     * @param gitabaseId Unique identifier for the database
     */
    fun closeDatabase(gitabaseId: GitabaseID)

    /**
     * Get number of open database instances
     * @return Number of currently open databases
     */
    fun getOpenDatabaseCount(): Int

    /**
     * Check if database is open
     * @param gitabaseId Unique identifier for the database
     * @return true if database is open, false otherwise
     */
    fun isDatabaseOpen(gitabaseId: GitabaseID): Boolean
}
