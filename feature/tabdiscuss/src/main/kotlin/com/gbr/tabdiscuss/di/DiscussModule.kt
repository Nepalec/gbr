package com.gbr.tabdiscuss.di

import com.gbr.tabdiscuss.navigation.DiscussFeature
import com.gbr.tabdiscuss.navigation.DiscussFeatureImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DiscussModule {

    @Binds
    abstract fun bindDiscussFeature(
        discussFeatureImpl: DiscussFeatureImpl
    ): DiscussFeature
}
