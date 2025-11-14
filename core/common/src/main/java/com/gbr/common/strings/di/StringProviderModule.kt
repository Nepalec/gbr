package com.gbr.common.strings.di

import com.gbr.common.strings.StringProvider
import com.gbr.common.strings.StringProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StringProviderModule {
    @Binds
    @Singleton
    abstract fun bindStringProvider(impl: StringProviderImpl): StringProvider
}