package com.gbr.data.repository

import android.util.Log
import com.gbr.datastore.datasource.GbrPreferencesDataSource
import com.gbr.model.theme.DarkThemeConfig
import com.gbr.model.gitabase.GitabaseID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of UserPreferencesRepository that delegates to GbrPreferencesDataSource.
 * This provides a clean abstraction between the domain layer and infrastructure layer.
 */
@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    private val gbrPreferencesDataSource: GbrPreferencesDataSource
) : UserPreferencesRepository {

    companion object {
        private const val TAG = "UserPreferencesRepository"
    }

    override suspend fun setLastUsedGitabase(gitabaseId: GitabaseID) {
        try {
            gbrPreferencesDataSource.setLastUsedGitabase(gitabaseId)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save last used gitabase", e)
            throw e // Re-throw to let the caller handle the error
        }
    }

    override suspend fun getLastUsedGitabase(): GitabaseID? {
        return try {
            gbrPreferencesDataSource.getLastUsedGitabase()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get last used gitabase", e)
            null
        }
    }

    override suspend fun setAppTheme(themeConfig: DarkThemeConfig) {
        try {
            gbrPreferencesDataSource.setDarkThemeConfig(themeConfig)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save app theme", e)
            throw e // Re-throw to let the caller handle the error
        }
    }

    override suspend fun getAppTheme(): DarkThemeConfig {
        return try {
            // Get the current theme from the userData flow
            gbrPreferencesDataSource.userData.map { userData ->
                userData.darkThemeConfig
            }.firstOrNull() ?: DarkThemeConfig.FOLLOW_SYSTEM
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get app theme", e)
            DarkThemeConfig.FOLLOW_SYSTEM // Return default on error
        }
    }

    override fun getAppThemeFlow(): Flow<DarkThemeConfig> {
        return gbrPreferencesDataSource.userData.map { userData ->
            userData.darkThemeConfig
        }
    }
}
