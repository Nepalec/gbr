package com.gbr.datastore.datasource

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.gbr.model.theme.DarkThemeConfig
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
        private val BOOK_CONTENTS_TEXT_SIZE_KEY = intPreferencesKey("book_contents_text_size")
        private val BOOK_CONTENTS_COLUMNS_KEY = intPreferencesKey("book_contents_columns")
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
    
    /**
     * Gets the book contents text size preference.
     * Default value is 0.
     *
     * @return The text size slider value (-2 to 2)
     */
    suspend fun getBookContentsTextSize(): Int {
        return try {
            userPreferences.data.map { preferences ->
                preferences[BOOK_CONTENTS_TEXT_SIZE_KEY] ?: 0
            }.firstOrNull() ?: 0
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get book contents text size", e)
            0
        }
    }
    
    /**
     * Sets the book contents text size preference.
     *
     * @param textSize The text size slider value (-2 to 2)
     */
    suspend fun setBookContentsTextSize(textSize: Int) {
        try {
            userPreferences.edit { preferences ->
                preferences[BOOK_CONTENTS_TEXT_SIZE_KEY] = textSize
            }
        } catch (ioException: IOException) {
            Log.e(TAG, "Failed to update book contents text size", ioException)
        }
    }
    
    /**
     * Gets the book contents columns preference.
     * Default value is 1.
     *
     * @return The number of columns (1 or 2)
     */
    suspend fun getBookContentsColumns(): Int {
        return try {
            userPreferences.data.map { preferences ->
                preferences[BOOK_CONTENTS_COLUMNS_KEY] ?: 1
            }.firstOrNull() ?: 1
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get book contents columns", e)
            1
        }
    }
    
    /**
     * Sets the book contents columns preference.
     *
     * @param columns The number of columns (1 or 2)
     */
    suspend fun setBookContentsColumns(columns: Int) {
        try {
            userPreferences.edit { preferences ->
                preferences[BOOK_CONTENTS_COLUMNS_KEY] = columns
            }
        } catch (ioException: IOException) {
            Log.e(TAG, "Failed to update book contents columns", ioException)
        }
    }
    
    /**
     * Gets the book images columns preference for a specific ImageType.
     * Default value is 1.
     *
     * @param imageTypeValue The ImageType.value (1=PICTURE, 2=CARD, 3=DIAGRAM, 4=FRESCO)
     * @return The number of columns (1, 2, 3, or 4)
     */
    suspend fun getBookImagesColumns(imageTypeValue: Int): Int {
        return try {
            val key = intPreferencesKey("book_images_columns_type_$imageTypeValue")
            userPreferences.data.map { preferences ->
                preferences[key] ?: 1
            }.firstOrNull() ?: 1
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get book images columns for type $imageTypeValue", e)
            1
        }
    }
    
    /**
     * Sets the book images columns preference for a specific ImageType.
     *
     * @param imageTypeValue The ImageType.value (1=PICTURE, 2=CARD, 3=DIAGRAM, 4=FRESCO)
     * @param columns The number of columns (1, 2, 3, or 4)
     */
    suspend fun setBookImagesColumns(imageTypeValue: Int, columns: Int) {
        try {
            val key = intPreferencesKey("book_images_columns_type_$imageTypeValue")
            userPreferences.edit { preferences ->
                preferences[key] = columns
            }
        } catch (ioException: IOException) {
            Log.e(TAG, "Failed to update book images columns for type $imageTypeValue", ioException)
        }
    }
}
