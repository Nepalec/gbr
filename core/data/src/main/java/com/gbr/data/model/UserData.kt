package com.gbr.data.model

import com.gbr.datastore.model.DarkThemeConfig
import com.gbr.model.gitabase.GitabaseID

/**
 * Represents the user's app data and preferences.
 */
data class UserData(
    val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    val lastUsedGitabase: GitabaseID? = null
)
