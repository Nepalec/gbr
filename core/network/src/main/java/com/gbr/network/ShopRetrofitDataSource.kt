package com.gbr.network

import androidx.tracing.trace
import com.gbr.network.model.NetworkShopContentResp
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Retrofit API declaration for NIA Network API
 */
private interface RetrofitShopNetworkApi {
    @GET(value = "get_shop_root.php")
    suspend fun getShopRoot(
        @Query("lang") lang: String,
    ): NetworkShopContentResp

}

private const val BASE_URL = BuildConfig.SHOP_BASE_URL

@Singleton
public class ShopRetrofitDataSource @Inject constructor(
    networkJson: Json,
    okhttpCallFactory: dagger.Lazy<Call.Factory>,
) : IShopDataSource {

    private val networkApi = trace("RetrofitShopNetwork") {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // We use callFactory lambda here with dagger.Lazy<Call.Factory>
            // to prevent initializing OkHttp on the main thread.
            .callFactory { okhttpCallFactory.get().newCall(it) }
            .addConverterFactory(
                networkJson.asConverterFactory("application/json".toMediaType()),
            )
            .build()
            .create(RetrofitShopNetworkApi::class.java)
    }

    override suspend fun getShopContents(lang: String): NetworkShopContentResp = networkApi.getShopRoot(lang)

}
