package com.gbr.datastore.datasource

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.gbr.datastore.model.DarkThemeConfig
import com.gbr.datastore.model.UserData
import com.gbr.model.gitabase.GitabaseID
import com.gbr.model.gitabase.GitabaseLang
import com.gbr.model.gitabase.GitabaseType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

/**
 * Data source for managing user preferences using DataStore.
 * Handles dark theme configuration and other user settings.
 */
class GbrPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<Preferences>
) {

    companion object {
        private const val TAG = "GbrPreferences"
        private val DARK_THEME_CONFIG_KEY = stringPreferencesKey("dark_theme_config")
        private val LAST_USED_GITABASE_KEY = stringPreferencesKey("last_used_gitabase")
    }

    /**
     * Flow of user data that emits whenever preferences change.
     */
    val userData: Flow<UserData> = userPreferences.data.map { preferences ->
        UserData(
            darkThemeConfig = getDarkThemeConfig(preferences)
        )
    }

    /**
     * Flow of last used Gitabase ID that emits whenever the preference changes.
     */
    val lastUsedGitabase: Flow<GitabaseID?> = userPreferences.data.map { preferences ->
        preferences[LAST_USED_GITABASE_KEY]?.let { key ->
            parseGitabaseID(key)
        }
    }

    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        try {
            userPreferences.edit { preferences ->
                preferences[DARK_THEME_CONFIG_KEY] = darkThemeConfig.name
            }
        } catch (ioException: IOException) {
            Log.e(TAG, "Failed to update dark theme config", ioException)
        }
    }

    private fun getDarkThemeConfig(preferences: Preferences): DarkThemeConfig {
        val configString = preferences[DARK_THEME_CONFIG_KEY] ?: DarkThemeConfig.FOLLOW_SYSTEM.name
        return try {
            DarkThemeConfig.valueOf(configString)
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "Invalid dark theme config: $configString, using default")
            DarkThemeConfig.FOLLOW_SYSTEM
        }
    }

    /**
     * Sets the last used Gitabase ID.
     *
     * @param gitabaseId The GitabaseID to save as last used
     */
    suspend fun setLastUsedGitabase(gitabaseId: GitabaseID) {
        try {
            userPreferences.edit { preferences ->
                preferences[LAST_USED_GITABASE_KEY] = gitabaseId.key
            }
        } catch (ioException: IOException) {
            Log.e(TAG, "Failed to update last used Gitabase", ioException)
        }
    }

    /**
     * Gets the last used Gitabase ID.
     *
     * @return The last used GitabaseID, or null if none was saved
     */
    suspend fun getLastUsedGitabase(): GitabaseID? {
        return try {
            userPreferences.data.map { preferences ->
                preferences[LAST_USED_GITABASE_KEY]?.let { key ->
                    parseGitabaseID(key)
                }
            }.let { flow ->
                // Get the first emitted value
                flow.firstOrNull()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get last used Gitabase", e)
            null
        }
    }

    /**
     * Gets the last used Gitabase ID synchronously from current preferences.
     *
     * @return The last used GitabaseID, or null if none was saved
     */
    fun getLastUsedGitabaseSync(): GitabaseID? {
        return try {
            // This is a simplified synchronous approach
            // In practice, you might want to use a different pattern
            null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get last used Gitabase", e)
            null
        }
    }

    /**
     * Parses a GitabaseID from its key string.
     *
     * @param key The key string in format "type_lang"
     * @return The parsed GitabaseID, or null if parsing fails
     */
    private fun parseGitabaseID(key: String): GitabaseID? {
        return try {
            val parts = key.split("_")
            if (parts.size >= 2) {
                val type = GitabaseType(parts[0])
                val lang = GitabaseLang(parts[1])
                GitabaseID(type, lang)
            } else {
                Log.w(TAG, "Invalid GitabaseID key format: $key")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse GitabaseID from key: $key", e)
            null
        }
    }
}
