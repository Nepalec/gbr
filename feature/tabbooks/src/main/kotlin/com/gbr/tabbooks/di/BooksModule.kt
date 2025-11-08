package com.gbr.tabbooks.di

import com.gbr.tabbooks.navigation.BooksFeature
import com.gbr.tabbooks.navigation.BooksFeatureImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BooksModule {

    @Binds
    @Singleton
    abstract fun bindBooksFeature(impl: BooksFeatureImpl): BooksFeature
}
