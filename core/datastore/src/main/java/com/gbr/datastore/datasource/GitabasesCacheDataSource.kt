package com.gbr.datastore.datasource

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.gbr.datastore.model.CachedGitabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source for caching Gitabases list using DataStore.
 * Stores the list of gitabases from successful API responses for offline access.
 */
@Singleton
class GitabasesCacheDataSource @Inject constructor(
    private val userPreferences: DataStore<Preferences>,
    private val json: Json
) {

    companion object {
        private const val TAG = "GitabasesCache"
        private val GITABASES_CACHE_KEY = stringPreferencesKey("gitabases_cache")
        private val CACHE_TIMESTAMP_KEY = stringPreferencesKey("gitabases_cache_timestamp")
    }

    /**
     * Flow of cached gitabases that emits whenever the cache changes.
     */
    val cachedGitabases: Flow<List<CachedGitabase>?> = userPreferences.data.map { preferences ->
        preferences[GITABASES_CACHE_KEY]?.let { cachedJson ->
            try {
                json.decodeFromString<List<CachedGitabase>>(cachedJson)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to deserialize cached gitabases", e)
                null
            }
        }
    }

    /**
     * Flow of cache timestamp that emits whenever the cache is updated.
     */
    val cacheTimestamp: Flow<Long?> = userPreferences.data.map { preferences ->
        preferences[CACHE_TIMESTAMP_KEY]?.toLongOrNull()
    }

    /**
     * Saves the gitabases list to cache.
     *
     * @param gitabases The list of gitabases to cache
     */
    suspend fun saveGitabases(gitabases: List<CachedGitabase>) {
        try {
            val jsonString = json.encodeToString(gitabases)
            val timestamp = System.currentTimeMillis()
            
            userPreferences.edit { preferences ->
                preferences[GITABASES_CACHE_KEY] = jsonString
                preferences[CACHE_TIMESTAMP_KEY] = timestamp.toString()
            }
            Log.d(TAG, "Successfully cached ${gitabases.size} gitabases")
        } catch (ioException: IOException) {
            Log.e(TAG, "Failed to cache gitabases", ioException)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to serialize gitabases for caching", e)
        }
    }

    /**
     * Gets the cached gitabases list.
     *
     * @return The cached list of gitabases, or null if not available
     */
    suspend fun getCachedGitabases(): List<CachedGitabase>? {
        return try {
            userPreferences.data.map { preferences ->
                preferences[GITABASES_CACHE_KEY]?.let { cachedJson ->
                    try {
                        json.decodeFromString<List<CachedGitabase>>(cachedJson)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to deserialize cached gitabases", e)
                        null
                    }
                }
            }.firstOrNull()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get cached gitabases", e)
            null
        }
    }

    /**
     * Gets the cache timestamp.
     *
     * @return The timestamp when the cache was last updated, or null if not available
     */
    suspend fun getCacheTimestamp(): Long? {
        return try {
            userPreferences.data.map { preferences ->
                preferences[CACHE_TIMESTAMP_KEY]?.toLongOrNull()
            }.firstOrNull()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get cache timestamp", e)
            null
        }
    }

    /**
     * Clears the gitabases cache.
     */
    suspend fun clearCache() {
        try {
            userPreferences.edit { preferences ->
                preferences.remove(GITABASES_CACHE_KEY)
                preferences.remove(CACHE_TIMESTAMP_KEY)
            }
            Log.d(TAG, "Successfully cleared gitabases cache")
        } catch (ioException: IOException) {
            Log.e(TAG, "Failed to clear gitabases cache", ioException)
        }
    }

    /**
     * Checks if the cache is valid based on age.
     *
     * @param maxAgeMillis Maximum age of cache in milliseconds (default: 24 hours)
     * @return true if cache exists and is not expired, false otherwise
     */
    suspend fun isCacheValid(maxAgeMillis: Long = 24 * 60 * 60 * 1000): Boolean {
        return try {
            val timestamp = getCacheTimestamp()
            val currentTime = System.currentTimeMillis()
            timestamp != null && (currentTime - timestamp) <= maxAgeMillis
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check cache validity", e)
            false
        }
    }
}
