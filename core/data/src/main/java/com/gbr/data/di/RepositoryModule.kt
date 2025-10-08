package com.gbr.data.di

import com.gbr.data.repository.GitabaseFilesRepo
import com.gbr.data.repository.GitabaseFilesRepoImpl
import com.gbr.data.repository.GitabaseTextsRepo
import com.gbr.data.repository.GitabaseTextsRepoImpl
import com.gbr.data.repository.UserDataRepository
import com.gbr.data.repository.UserDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGitabaseFilesRepo(gitabaseFilesRepoImpl: GitabaseFilesRepoImpl): GitabaseFilesRepo

    @Binds
    @Singleton
    abstract fun bindGitabaseTextsRepo(gitabaseTextsRepoImpl: GitabaseTextsRepoImpl): GitabaseTextsRepo

    @Binds
    @Singleton
    abstract fun bindUserDataRepository(userDataRepositoryImpl: UserDataRepositoryImpl): UserDataRepository
}
