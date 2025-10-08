package com.gbr.data.repository

import com.gbr.data.model.UserData
import com.gbr.datastore.model.DarkThemeConfig
import com.gbr.model.gitabase.GitabaseID
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing user data and preferences.
 * Provides access to user settings and preferences through a clean API.
 */
interface UserDataRepository {

    /**
     * Flow of user data that emits whenever user preferences change.
     */
    val userData: Flow<UserData>

    /**
     * Sets the dark theme configuration.
     *
     * @param darkThemeConfig The dark theme configuration to set
     */
    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig)

    /**
     * Sets the last used Gitabase ID.
     *
     * @param gitabaseId The GitabaseID to save as last used
     */
    suspend fun setLastUsedGitabase(gitabaseId: GitabaseID)

    /**
     * Gets the last used Gitabase ID.
     *
     * @return The last used GitabaseID, or null if none was saved
     */
    suspend fun getLastUsedGitabase(): GitabaseID?

    /**
     * Flow of last used Gitabase ID that emits whenever the preference changes.
     */
    val lastUsedGitabase: Flow<GitabaseID?>
}
