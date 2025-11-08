package com.gbr.data.model

import com.gbr.model.gitabase.GitabaseID
import com.gbr.model.theme.DarkThemeConfig

/**
 * Represents the user's app data and preferences.
 */
data class UserData(
    val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
    val lastUsedGitabase: GitabaseID? = null
)
