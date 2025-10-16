package com.gbr.data.di

import com.gbr.data.repository.GitabasesRepository
import com.gbr.data.repository.GitabasesRepositoryImpl
import com.gbr.data.repository.GitabaseTextsRepo
import com.gbr.data.repository.GitabaseTextsRepoImpl
import com.gbr.data.repository.GitabasesDescRepository
import com.gbr.data.repository.GitabasesDescRepositoryImpl
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
    abstract fun bindGitabasesRepository(gitabasesRepositoryImpl: GitabasesRepositoryImpl): GitabasesRepository

    @Binds
    @Singleton
    abstract fun bindGitabaseTextsRepo(gitabaseTextsRepoImpl: GitabaseTextsRepoImpl): GitabaseTextsRepo

    @Binds
    @Singleton
    abstract fun bindUserDataRepository(userDataRepositoryImpl: UserDataRepositoryImpl): UserDataRepository

    @Binds
    @Singleton
    abstract fun bindGitabasesDescRepository(gitabasesDescRepositoryImpl: GitabasesDescRepositoryImpl): GitabasesDescRepository
}
