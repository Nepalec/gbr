package com.gbr.datasource.di

import com.gbr.datasource.LocalFileDataSource
import com.gbr.datasource.LocalFileDataSourceImpl
import com.gbr.datasource.RemoteFileDataSource
import com.gbr.datasource.RemoteFileDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
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
}

