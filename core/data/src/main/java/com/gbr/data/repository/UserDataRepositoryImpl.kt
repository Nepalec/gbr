package com.gbr.data.repository

import com.gbr.data.model.UserData
import com.gbr.datastore.datasource.GbrPreferencesDataSource
import com.gbr.model.book.BookContentsTabOptions
import com.gbr.model.book.BookImagesTabOptions
import com.gbr.model.gitabase.ImageType
import com.gbr.model.theme.DarkThemeConfig
import com.gbr.model.gitabase.GitabaseID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of UserDataRepository that uses GbrPreferencesDataSource.
 * Combines data from multiple sources to provide a unified user data experience.
 */
@Singleton
class UserDataRepositoryImpl @Inject constructor(
    private val gbrPreferencesDataSource: GbrPreferencesDataSource
) : UserDataRepository {

    /**
     * Flow of user data that combines dark theme and last used Gitabase.
     */
    override val userData: Flow<UserData> = combine(
        gbrPreferencesDataSource.userData,
        gbrPreferencesDataSource.lastUsedGitabase
    ) { datastoreUserData, lastUsedGitabase ->
        UserData(
            darkThemeConfig = datastoreUserData.darkThemeConfig,
            lastUsedGitabase = lastUsedGitabase
        )
    }

    /**
     * Sets the dark theme configuration.
     */
    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        gbrPreferencesDataSource.setDarkThemeConfig(darkThemeConfig)
    }

    /**
     * Sets the last used Gitabase ID.
     */
    override suspend fun setLastUsedGitabase(gitabaseId: GitabaseID) {
        gbrPreferencesDataSource.setLastUsedGitabase(gitabaseId)
    }

    /**
     * Gets the last used Gitabase ID.
     */
    override suspend fun getLastUsedGitabase(): GitabaseID? {
        return gbrPreferencesDataSource.getLastUsedGitabase()
    }

    /**
     * Flow of last used Gitabase ID that emits whenever the preference changes.
     */
    override val lastUsedGitabase: Flow<GitabaseID?> = gbrPreferencesDataSource.lastUsedGitabase
    
    /**
     * Gets the book contents tab options preference.
     */
    override suspend fun getBookContentsTabOptions(): BookContentsTabOptions {
        return gbrPreferencesDataSource.getBookContentsTabOptions()
    }
    
    /**
     * Sets the book contents tab options preference.
     */
    override suspend fun setBookContentsTabOptions(options: BookContentsTabOptions) {
        gbrPreferencesDataSource.setBookContentsTabOptions(options)
    }
    
    /**
     * Gets the book images tab options preference for a specific ImageType.
     */
    override suspend fun getBookImagesTabOptions(imageType: ImageType): BookImagesTabOptions {
        return gbrPreferencesDataSource.getBookImagesTabOptions(imageType)
    }
    
    /**
     * Sets the book images tab options preference for a specific ImageType.
     */
    override suspend fun setBookImagesTabOptions(imageType: ImageType, options: BookImagesTabOptions) {
        gbrPreferencesDataSource.setBookImagesTabOptions(imageType, options)
    }
}
