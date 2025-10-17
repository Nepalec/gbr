package com.gbr.data.database

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.gbr.model.gitabase.GitabaseID
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages a cache of GitabaseDatabase instances to optimize performance
 * when switching between multiple gitabase files.
 * 
 * Uses LRU (Least Recently Used) eviction strategy to limit memory usage
 * while keeping frequently accessed databases open.
 */
@Singleton
class GitabaseDatabaseManager @Inject constructor(
    private val context: Context,
    private val gitabaseFolderPath: String,
    private val maxCacheSize: Int = 3
) {
    companion object {
        private const val TAG = "GitabaseDatabaseManager"
    }

    // LinkedHashMap with access-order mode (true) for LRU behavior
    // When accessed, entries move to the end of the map
    // The first entry is always the least recently used
    private val databaseCache = LinkedHashMap<GitabaseID, GitabaseDatabase>(
        maxCacheSize,
        0.75f,  // load factor
        true     // access-order mode (vs insertion-order)
    )

    /**
     * Gets a database instance for the specified gitabase.
     * Returns cached instance if available, otherwise opens the database
     * and adds it to the cache.
     * 
     * Thread-safe: synchronized to prevent concurrent modifications
     * 
     * @param gitabaseId The ID of the gitabase
     * @return GitabaseDatabase instance (cached or newly opened)
     */
    @Synchronized
    fun getDatabase(gitabaseId: GitabaseID): GitabaseDatabase {
        // Check if database is already in cache
        val cachedDb = databaseCache[gitabaseId]
        if (cachedDb != null) {
            Log.d(TAG, "Cache HIT for gitabase: ${gitabaseId.key}")
            return cachedDb
        }

        Log.d(TAG, "Cache MISS for gitabase: ${gitabaseId.key}, opening database...")

        // If cache is full, evict the least recently used database
        if (databaseCache.size >= maxCacheSize) {
            evictLRU()
        }

        // Construct file path and open the database
        val filePath = gitabaseId.getFilePath(gitabaseFolderPath)
        val database = openDatabase(filePath)
        
        // Add to cache
        databaseCache[gitabaseId] = database
        Log.d(TAG, "Database cached for gitabase: ${gitabaseId.key} (cache size: ${databaseCache.size})")

        return database
    }

    /**
     * Opens a Room database from a specific file path.
     * 
     * @param filePath The absolute path to the .db file
     * @return GitabaseDatabase instance
     * @throws IllegalArgumentException if file doesn't exist
     */
    private fun openDatabase(filePath: String): GitabaseDatabase {
        val dbFile = File(filePath)
        
        if (!dbFile.exists()) {
            throw IllegalArgumentException("Database file does not exist: $filePath")
        }

        if (!dbFile.canRead()) {
            throw IllegalArgumentException("Cannot read database file: $filePath")
        }

        // Create a unique database name based on the file path
        // This prevents Room from using the same instance for different files
        val dbName = "gitabase_${dbFile.nameWithoutExtension}_${dbFile.hashCode()}"

        return Room.databaseBuilder(
            context.applicationContext,
            GitabaseDatabase::class.java,
            dbName
        )
        .createFromFile(dbFile)  // Use existing .db file
        .fallbackToDestructiveMigration()
        .build()
    }

    /**
     * Evicts the least recently used database from the cache.
     * The first entry in LinkedHashMap (access-order mode) is the LRU.
     * 
     * Closes the database before removing it from cache to free resources.
     */
    private fun evictLRU() {
        // In access-order mode, the first entry is the least recently used
        val lruEntry = databaseCache.entries.firstOrNull()
        
        if (lruEntry != null) {
            Log.d(TAG, "Evicting LRU database: ${lruEntry.key.key}")
            
            try {
                // Close the database to free resources
                lruEntry.value.close()
            } catch (e: Exception) {
                Log.e(TAG, "Error closing database during eviction: ${lruEntry.key.key}", e)
            }
            
            // Remove from cache
            databaseCache.remove(lruEntry.key)
            Log.d(TAG, "Database evicted. New cache size: ${databaseCache.size}")
        }
    }

    /**
     * Manually closes and removes a specific database from the cache.
     * Useful when a gitabase is deleted or needs to be refreshed.
     * 
     * @param gitabaseId The ID of the gitabase to close
     */
    @Synchronized
    fun closeDatabase(gitabaseId: GitabaseID) {
        val database = databaseCache.remove(gitabaseId)
        
        if (database != null) {
            try {
                database.close()
                Log.d(TAG, "Manually closed database: ${gitabaseId.key}")
            } catch (e: Exception) {
                Log.e(TAG, "Error closing database: ${gitabaseId.key}", e)
            }
        } else {
            Log.d(TAG, "Database not in cache, nothing to close: ${gitabaseId.key}")
        }
    }

    /**
     * Closes all cached databases and clears the cache.
     * Should be called when the app is being terminated or when
     * all databases need to be refreshed.
     */
    @Synchronized
    fun closeAll() {
        Log.d(TAG, "Closing all cached databases (count: ${databaseCache.size})")
        
        databaseCache.forEach { (gitabaseId, database) ->
            try {
                database.close()
                Log.d(TAG, "Closed database: ${gitabaseId.key}")
            } catch (e: Exception) {
                Log.e(TAG, "Error closing database: ${gitabaseId.key}", e)
            }
        }
        
        databaseCache.clear()
        Log.d(TAG, "All databases closed, cache cleared")
    }

    /**
     * Returns the current number of cached databases.
     * Useful for monitoring and testing.
     */
    @Synchronized
    fun getCacheSize(): Int = databaseCache.size

    /**
     * Checks if a database is currently cached.
     * Useful for testing and debugging.
     */
    @Synchronized
    fun isCached(gitabaseId: GitabaseID): Boolean = databaseCache.containsKey(gitabaseId)

    /**
     * Gets a list of currently cached gitabase IDs.
     * Useful for monitoring and debugging.
     */
    @Synchronized
    fun getCachedGitabases(): List<GitabaseID> = databaseCache.keys.toList()
}
