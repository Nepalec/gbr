package com.gbr.network

import androidx.tracing.trace
import com.gbr.common.strings.StringProvider
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
    @GET(value = "gb/json/droid/gbr/get_gitabases_description.php")
    suspend fun getGitabasesDesc(): NetworkGitabasesDescResp

    @GET(value = "gb/json/droid/gbr/get_gitabases_4download.php")
    suspend fun getGitabases4Download(): NetworkGitabasesDescResp
}

private const val BASE_URL = BuildConfig.BASE_URL

@Singleton
public class GitabasesDescRetrofitDataSource @Inject constructor(
    networkJson: Json,
    okhttpCallFactory: dagger.Lazy<Call.Factory>,
    private val stringProvider: StringProvider
) : IGitabasesDescDataSource {

    private val networkApi = trace("RetrofitGitabasesDescNetwork") {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // We use callFactory lambda here with dagger.Lazy<Call.Factory>
            // to prevent initializing OkHttp on the main thread.
            .callFactory { okhttpCallFactory.get().newCall(it) }
            .addConverterFactory(
                networkJson.asConverterFactory("application/json".toMediaType())
            )
            .build()
            .create(RetrofitGitabasesDescNetworkApi::class.java)
    }

    override suspend fun getGitabasesDesc(is4Download: Boolean): NetworkGitabasesDescResp {
        return try {
            if (is4Download) networkApi.getGitabases4Download() else networkApi.getGitabasesDesc()
        } catch (e: HttpException) {
            // Handle HTTP errors in network layer
            when (e.code()) {
                404 -> NetworkGitabasesDescResp(
                    gitabases = emptyList(),
                    success = 0,
                    message = stringProvider.getString(R.string.error_api_endpoint_not_found)
                )

                else -> NetworkGitabasesDescResp(
                    gitabases = emptyList(),
                    success = 0,
                    message = stringProvider.getString(R.string.error_http_error, e.code(), e.message() ?: "")
                )
            }
        } catch (e: Exception) {
            // Handle other network errors
            NetworkGitabasesDescResp(
                gitabases = emptyList(),
                success = 0,
                message = stringProvider.getString(R.string.error_network_error, e.message ?: "")
            )
        }
    }
}