package com.gbr.data.repository

import com.gbr.model.book.BookContentsTabOptions
import com.gbr.model.book.BookImagesTabOptions
import com.gbr.model.gitabase.GitabaseID
import com.gbr.model.gitabase.ImageType
import com.gbr.model.theme.DarkThemeConfig
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    suspend fun setLastUsedGitabase(gitabaseId: GitabaseID)
    val lastUsedGitabase: Flow<GitabaseID?>

    suspend fun setAppTheme(themeConfig: DarkThemeConfig)
    val appTheme: Flow<DarkThemeConfig>

    suspend fun setBookContentsTabOptions(options: BookContentsTabOptions)
    val bookContentsTabOptions: Flow<BookContentsTabOptions>

    suspend fun setBookImagesTabOptions(imageType: ImageType, options: BookImagesTabOptions)

    fun getBookImagesTabOptionsFlow(imageType: ImageType): Flow<BookImagesTabOptions>
}