package com.gbr.data.example

import com.gbr.data.repository.UserDataRepository
import com.gbr.datastore.model.DarkThemeConfig
import com.gbr.model.gitabase.GitabaseID
import com.gbr.model.gitabase.GitabaseLang
import com.gbr.model.gitabase.GitabaseType
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

/**
 * Example of how to use UserDataRepository.
 * This class demonstrates the usage patterns for the user data repository.
 */
class UserDataRepositoryExample @Inject constructor(
    private val userDataRepository: UserDataRepository
) {

    /**
     * Example: Observe user data changes
     */
    suspend fun observeUserData() {
        userDataRepository.userData.collect { userData ->
            when (userData.darkThemeConfig) {
                DarkThemeConfig.FOLLOW_SYSTEM -> {
                    println("Using system theme")
                }
                DarkThemeConfig.LIGHT -> {
                    println("Using light theme")
                }
                DarkThemeConfig.DARK -> {
                    println("Using dark theme")
                }
            }

            userData.lastUsedGitabase?.let { gitabaseId ->
                println("Last used Gitabase: ${gitabaseId.key}")
            }
        }
    }

    /**
     * Example: Set dark theme to light
     */
    suspend fun setLightTheme() {
        userDataRepository.setDarkThemeConfig(DarkThemeConfig.LIGHT)
    }

    /**
     * Example: Set dark theme to dark
     */
    suspend fun setDarkTheme() {
        userDataRepository.setDarkThemeConfig(DarkThemeConfig.DARK)
    }

    /**
     * Example: Set dark theme to follow system
     */
    suspend fun setSystemTheme() {
        userDataRepository.setDarkThemeConfig(DarkThemeConfig.FOLLOW_SYSTEM)
    }

    /**
     * Example: Observe last used Gitabase changes
     */
    suspend fun observeLastUsedGitabase() {
        userDataRepository.lastUsedGitabase.collect { gitabaseId ->
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
        userDataRepository.setLastUsedGitabase(gitabaseId)
    }

    /**
     * Example: Get last used Gitabase
     */
    suspend fun getLastUsedGitabase() {
        val lastUsed = userDataRepository.getLastUsedGitabase()
        if (lastUsed != null) {
            println("Retrieved last used Gitabase: ${lastUsed.key}")
        } else {
            println("No last used Gitabase found")
        }
    }
}
