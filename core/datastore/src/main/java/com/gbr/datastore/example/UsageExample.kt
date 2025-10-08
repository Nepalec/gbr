package com.gbr.datastore.example

import com.gbr.datastore.datasource.GbrPreferencesDataSource
import com.gbr.datastore.model.DarkThemeConfig
import com.gbr.model.gitabase.GitabaseID
import com.gbr.model.gitabase.GitabaseLang
import com.gbr.model.gitabase.GitabaseType
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

/**
 * Example of how to use GbrPreferencesDataSource.
 * This class demonstrates the usage patterns for the preferences data source.
 */
class UsageExample @Inject constructor(
    private val gbrPreferencesDataSource: GbrPreferencesDataSource
) {

    /**
     * Example: Observe user preferences changes
     */
    suspend fun observeUserPreferences() {
        gbrPreferencesDataSource.userData.collect { userData ->
            when (userData.darkThemeConfig) {
                DarkThemeConfig.FOLLOW_SYSTEM -> {
                    // Apply system theme
                    println("Using system theme")
                }
                DarkThemeConfig.LIGHT -> {
                    // Apply light theme
                    println("Using light theme")
                }
                DarkThemeConfig.DARK -> {
                    // Apply dark theme
                    println("Using dark theme")
                }
            }
        }
    }

    /**
     * Example: Set dark theme to light
     */
    suspend fun setLightTheme() {
        gbrPreferencesDataSource.setDarkThemeConfig(DarkThemeConfig.LIGHT)
    }

    /**
     * Example: Set dark theme to dark
     */
    suspend fun setDarkTheme() {
        gbrPreferencesDataSource.setDarkThemeConfig(DarkThemeConfig.DARK)
    }

    /**
     * Example: Set dark theme to follow system
     */
    suspend fun setSystemTheme() {
        gbrPreferencesDataSource.setDarkThemeConfig(DarkThemeConfig.FOLLOW_SYSTEM)
    }

    /**
     * Example: Observe last used Gitabase changes
     */
    suspend fun observeLastUsedGitabase() {
        gbrPreferencesDataSource.lastUsedGitabase.collect { gitabaseId ->
            if (gitabaseId != null) {
                println("Last used Gitabase: ${gitabaseId.type.value}_${gitabaseId.lang.value}")
            } else {
                println("No last used Gitabase found")
            }
        }
    }

    /**
     * Example: Set last used Gitabase
     */
    suspend fun setLastUsedGitabase() {
        val gitabaseId = GitabaseID(
            type = GitabaseType.HELP,
            lang = GitabaseLang.ENG
        )
        gbrPreferencesDataSource.setLastUsedGitabase(gitabaseId)
    }

    /**
     * Example: Get last used Gitabase
     */
    suspend fun getLastUsedGitabase() {
        val lastUsed = gbrPreferencesDataSource.getLastUsedGitabase()
        if (lastUsed != null) {
            println("Retrieved last used Gitabase: ${lastUsed.key}")
        } else {
            println("No last used Gitabase found")
        }
    }
}
