package com.gbr.datasource.di

import android.content.Context
import androidx.room.Room
import com.gbr.datasource.LocalFileDataSource
import com.gbr.datasource.LocalFileDataSourceImpl
import com.gbr.datasource.RemoteFileDataSource
import com.gbr.datasource.RemoteFileDataSourceImpl
import com.gbr.datasource.notes.UserNotesDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun bindRemoteFileDataSource(
        remoteFileDataSourceImpl: RemoteFileDataSourceImpl
    ): RemoteFileDataSource

    @Binds
    @Singleton
    abstract fun bindLocalFileDataSource(
        localFileDataSourceImpl: LocalFileDataSourceImpl
    ): LocalFileDataSource

    companion object {
        @Provides
        @Singleton
        fun provideUserNotesDatabase(
            @ApplicationContext context: Context
        ): UserNotesDatabase {
            return Room.databaseBuilder(
                context,
                UserNotesDatabase::class.java,
                "user_notes_db"
            ).build()
        }
    }
}
