package com.gbr.datastore.datasource

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.gbr.datastore.model.CachedGitabase
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
        private const val CACHE_VALIDITY_MS =  7L * 24 * 60 * 60 * 1000 // 1 week in milliseconds
    }


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

    suspend fun getCachedGitabases(): List<CachedGitabase>? {
        return try {
            userPreferences.data.map { preferences ->
                val cachedJson = preferences[GITABASES_CACHE_KEY]
                val timestampString = preferences[CACHE_TIMESTAMP_KEY]

                if (cachedJson == null || timestampString == null) {
                    return@map null
                }

                // Check if cache is still valid (within 1 week)
                val timestamp = timestampString.toLongOrNull()
                if (timestamp == null) {
                    Log.w(TAG, "Invalid cache timestamp, treating as expired")
                    return@map null
                }

                val currentTime = System.currentTimeMillis()
                val cacheAge = currentTime - timestamp

                if (cacheAge > CACHE_VALIDITY_MS) {
                    Log.d(TAG, "Cache expired (age: ${cacheAge / (24 * 60 * 60 * 1000)} days)")
                    return@map null
                }

                try {
                    json.decodeFromString<List<CachedGitabase>>(cachedJson)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to deserialize cached gitabases", e)
                    null
                }
            }.firstOrNull()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get cached gitabases", e)
            null
        }
    }




}
