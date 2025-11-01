package com.gbr.data.repository

import com.gbr.data.model.UserData
import com.gbr.model.theme.DarkThemeConfig
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
    
    /**
     * Gets the book contents text size preference.
     *
     * @return The text size slider value (-2 to 2), default is 0
     */
    suspend fun getBookContentsTextSize(): Int
    
    /**
     * Sets the book contents text size preference.
     *
     * @param textSize The text size slider value (-2 to 2)
     */
    suspend fun setBookContentsTextSize(textSize: Int)
    
    /**
     * Gets the book contents columns preference.
     *
     * @return The number of columns (1 or 2), default is 1
     */
    suspend fun getBookContentsColumns(): Int
    
    /**
     * Sets the book contents columns preference.
     *
     * @param columns The number of columns (1 or 2)
     */
    suspend fun setBookContentsColumns(columns: Int)
    
    /**
     * Gets the book images columns preference for a specific ImageType.
     *
     * @param imageTypeValue The ImageType.value (1=PICTURE, 2=CARD, 3=DIAGRAM, 4=FRESCO)
     * @return The number of columns (1, 2, 3, or 4), default is 1
     */
    suspend fun getBookImagesColumns(imageTypeValue: Int): Int
    
    /**
     * Sets the book images columns preference for a specific ImageType.
     *
     * @param imageTypeValue The ImageType.value (1=PICTURE, 2=CARD, 3=DIAGRAM, 4=FRESCO)
     * @param columns The number of columns (1, 2, 3, or 4)
     */
    suspend fun setBookImagesColumns(imageTypeValue: Int, columns: Int)
}
