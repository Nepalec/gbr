package com.gbr.datastore.model

/**
 * Represents the user's app preferences and settings.
 */
data class UserData(
    val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM
)
