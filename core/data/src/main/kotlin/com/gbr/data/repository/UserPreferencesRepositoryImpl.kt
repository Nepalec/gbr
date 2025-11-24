package com.gbr.data.repository

import android.util.Log
import com.gbr.datastore.datasource.GbrPreferencesDataSource
import com.gbr.model.book.BookContentsTabOptions
import com.gbr.model.book.BookImagesTabOptions
import com.gbr.model.gitabase.GitabaseID
import com.gbr.model.gitabase.ImageType
import com.gbr.model.theme.DarkThemeConfig
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

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

    override val lastUsedGitabase: Flow<GitabaseID?> = gbrPreferencesDataSource.lastUsedGitabase

    override suspend fun setAppTheme(themeConfig: DarkThemeConfig) {
        try {
            gbrPreferencesDataSource.setDarkThemeConfig(themeConfig)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save app theme", e)
            throw e // Re-throw to let the caller handle the error
        }
    }

    override val appTheme: Flow<DarkThemeConfig> = gbrPreferencesDataSource.appTheme

    override suspend fun setBookContentsTabOptions(options: BookContentsTabOptions) {
        gbrPreferencesDataSource.setBookContentsTabOptions(options)
    }

    override val bookContentsTabOptions: Flow<BookContentsTabOptions> = gbrPreferencesDataSource.bookContentsTabOptions

    override suspend fun setBookImagesTabOptions(imageType: ImageType, options: BookImagesTabOptions) {
        gbrPreferencesDataSource.setBookImagesTabOptions(imageType, options)
    }

    override fun getBookImagesTabOptionsFlow(imageType: ImageType): Flow<BookImagesTabOptions> {
        return gbrPreferencesDataSource.getBookImagesTabOptionsFlow(imageType)
    }
}