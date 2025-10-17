package com.gbr.data.repository

import com.gbr.model.theme.DarkThemeConfig
import com.gbr.model.gitabase.GitabaseID
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing user preferences.
 * Abstracts the infrastructure layer (DataStore) from the domain layer.
 */
interface UserPreferencesRepository {
    /**
     * Saves the last used Gitabase ID to user preferences.
     *
     * @param gitabaseId The GitabaseID to save as last used
     */
    suspend fun setLastUsedGitabase(gitabaseId: GitabaseID)
    
    /**
     * Gets the last used Gitabase ID from user preferences.
     *
     * @return The last used GitabaseID, or null if none was saved
     */
    suspend fun getLastUsedGitabase(): GitabaseID?
    
    /**
     * Sets the app theme preference.
     *
     * @param themeConfig The theme configuration to save
     */
    suspend fun setAppTheme(themeConfig: DarkThemeConfig)
    
    /**
     * Gets the current app theme preference.
     *
     * @return The current theme configuration, or FOLLOW_SYSTEM if none was saved
     */
    suspend fun getAppTheme(): DarkThemeConfig
    
    /**
     * Gets the current app theme preference as a Flow.
     *
     * @return Flow of theme configuration that emits whenever the theme changes
     */
    fun getAppThemeFlow(): Flow<DarkThemeConfig>
}
