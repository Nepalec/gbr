package com.gbr.network.di

import android.content.Context
import androidx.tracing.trace
import coil.ImageLoader
import coil.util.DebugLogger
import com.gbr.network.GitabasesDescRetrofitDataSource
import com.gbr.network.IGitabasesDescDataSource
import com.gbr.network.INotesBackupImportDataSource
import com.gbr.network.IShopDataSource
import com.gbr.network.NotesBackupImportDataSource
import com.gbr.network.ShopRetrofitDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
public object NetworkModule {

    @Provides
    @Singleton
    fun providesNetworkJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        explicitNulls = false
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor()
                    .apply {
                        setLevel(HttpLoggingInterceptor.Level.BODY)
                    }
            )
            .build()
    }

    @Provides
    @Singleton
    fun okHttpCallFactory(okHttpClient: OkHttpClient): Call.Factory {
        return okHttpClient
    }

    @Provides
    @Singleton
    fun imageLoader(
        okHttpCallFactory: dagger.Lazy<Call.Factory>,
        @ApplicationContext application: Context
    ): ImageLoader = trace("ImageLoader") {
        ImageLoader.Builder(application)
            .callFactory { okHttpCallFactory.get() }
            // Assume most content images are versioned urls
            // but some problematic images are fetching each time
            .respectCacheHeaders(false)
            .apply {
                logger(DebugLogger())
            }
            .build()
    }
}

@Module
@InstallIn(SingletonComponent::class)
public abstract class NetworkBindingsModule {
    @Binds
    @Singleton
    abstract fun bindShopDataSource(shopRetrofitDataSource: ShopRetrofitDataSource): IShopDataSource

    @Binds
    @Singleton
    abstract fun bindGitabasesDescDataSource(
        gitabasesDescRetrofitDataSource: GitabasesDescRetrofitDataSource
    ): IGitabasesDescDataSource

    @Binds
    @Singleton
    abstract fun bindNotesBackupImportDataSource(
        notesBackupImportDataSource: NotesBackupImportDataSource
    ): INotesBackupImportDataSource
}