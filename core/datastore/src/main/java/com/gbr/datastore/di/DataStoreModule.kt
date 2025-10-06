package com.gbr.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// DataStore Module
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("gitabase_preferences")
        }
    }
}

// DataStore Keys
object GitabasePreferencesKeys {
    val CURRENT_DATABASE_TYPE = stringPreferencesKey("current_database_type")
    val CURRENT_DATABASE_LANGUAGE = stringPreferencesKey("current_database_language")
    val DEFAULT_FOLDER_PATH = stringPreferencesKey("default_folder_path")
    val USE_LEGACY_FOLDER = booleanPreferencesKey("use_legacy_folder")
    val LAST_SCAN_TIME = longPreferencesKey("last_scan_time")
}
