package com.gbr.network

import androidx.tracing.trace
import com.gbr.network.model.NetworkGitabasesDescResp
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Retrofit API declaration for GitabasesDesc Network API
 */
private interface RetrofitGitabasesDescNetworkApi {
    @GET(value = "gb/json/droid/get_gitabases_all.php")
    suspend fun getGitabasesDesc(): NetworkGitabasesDescResp
}

private const val BASE_URL = BuildConfig.BASE_URL

@Singleton
public class GitabasesDescRetrofitDataSource @Inject constructor(
    networkJson: Json,
    okhttpCallFactory: dagger.Lazy<Call.Factory>,
) : IGitabasesDescDataSource {

    private val networkApi = trace("RetrofitGitabasesDescNetwork") {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // We use callFactory lambda here with dagger.Lazy<Call.Factory>
            // to prevent initializing OkHttp on the main thread.
            .callFactory { okhttpCallFactory.get().newCall(it) }
            .addConverterFactory(
                networkJson.asConverterFactory("application/json".toMediaType()),
            )
            .build()
            .create(RetrofitGitabasesDescNetworkApi::class.java)
    }

    override suspend fun getGitabasesDesc(): NetworkGitabasesDescResp {
        return try {
            networkApi.getGitabasesDesc()
        } catch (e: HttpException) {
            // Handle HTTP errors in network layer
            when (e.code()) {
                404 -> NetworkGitabasesDescResp(
                    gitabases = emptyList(),
                    success = 0,
                    message = "API endpoint not found (404). Please check the URL configuration."
                )
                else -> NetworkGitabasesDescResp(
                    gitabases = emptyList(),
                    success = 0,
                    message = "HTTP error ${e.code()}: ${e.message()}"
                )
            }
        } catch (e: Exception) {
            // Handle other network errors
            NetworkGitabasesDescResp(
                gitabases = emptyList(),
                success = 0,
                message = "Network error: ${e.message}"
            )
        }
    }
}
