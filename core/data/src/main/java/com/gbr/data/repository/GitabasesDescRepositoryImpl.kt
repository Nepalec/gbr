package com.gbr.data.repository

import android.util.Log
import com.gbr.common.strings.StringProvider
import com.gbr.data.R
import com.gbr.data.mapper.GitabaseDescMapper
import com.gbr.data.model.GitabaseDescNetwork
import com.gbr.data.model.GitabasesDescResponse
import com.gbr.datastore.datasource.GitabasesCacheDataSource
import com.gbr.datastore.model.CachedGitabase
import com.gbr.model.gitabase.Gitabase
import com.gbr.network.IGitabasesDescDataSource
import com.gbr.network.model.NetworkGitabasesDescResp
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitabasesDescRepositoryImpl @Inject constructor(
    private val gitabasesDescDataSource: IGitabasesDescDataSource,
    private val gitabasesCacheDataSource: GitabasesCacheDataSource,
    private val stringProvider: StringProvider
) : GitabasesDescRepository {

    companion object {
        private const val TAG = "GitabasesDescRepository"
    }

    override suspend fun getGitabasesDesc(is4Download: Boolean): GitabasesDescResponse {
        return try {
            // Try to get fresh data from network
            val networkResponse = gitabasesDescDataSource.getGitabasesDesc(is4Download)

            // If successful, cache the gitabases list
            if (networkResponse.success == 1) {
                val cachedGitabases = networkResponse.gitabases.map { it.toCachedGitabase() }
                gitabasesCacheDataSource.saveGitabases(cachedGitabases)
                Log.d(TAG, "Successfully fetched and cached gitabases from network")
            }

            networkResponse.toGitabasesDescResponse()
        } catch (e: Exception) {
            Log.w(TAG, "Network request failed, trying cache", e)

            // If network fails, try to get from cache
            val cachedGitabases = gitabasesCacheDataSource.getCachedGitabases()
            if (cachedGitabases != null) {
                Log.d(TAG, "Using cached gitabases (${cachedGitabases.size} items)")
                GitabasesDescResponse(
                    gitabases = cachedGitabases.map { it.toGitabaseDescNetwork() },
                    success = 1,
                    message = stringProvider.getString(R.string.message_data_from_cache)
                )
            } else {
                Log.e(TAG, "No cached data available")
                GitabasesDescResponse(
                    gitabases = emptyList(),
                    success = 0,
                    message = stringProvider.getString(R.string.error_network_and_no_cache, e.message ?: "")
                )
            }
        }
    }

    override suspend fun getAllGitabases(): List<Gitabase> {
        val descResponse = getGitabasesDesc(false)
        return GitabaseDescMapper.toGitabaseList(descResponse.gitabases)
    }
    override suspend fun getDownloadableGitabases(): List<Gitabase> {
        val descResponse = getGitabasesDesc(true)
        return GitabaseDescMapper.toGitabaseList(descResponse.gitabases)
    }

    private fun NetworkGitabasesDescResp.toGitabasesDescResponse(): GitabasesDescResponse {
        return GitabasesDescResponse(
            gitabases = gitabases.map { it.toGitabaseDescNetwork() },
            success = success,
            message = message
        )
    }

    private fun NetworkGitabasesDescResp.Gitabase.toGitabaseDescNetwork(): GitabaseDescNetwork {
        return GitabaseDescNetwork(
            gbname = gbname,
            gbalias = gbalias,
            gblang = gblang,
            lastModified = lastModified
        )
    }

    private fun NetworkGitabasesDescResp.Gitabase.toCachedGitabase(): CachedGitabase {
        return CachedGitabase(
            gbname = gbname,
            gbalias = gbalias,
            gblang = gblang,
            lastModified = lastModified
        )
    }

    private fun CachedGitabase.toGitabaseDescNetwork(): GitabaseDescNetwork {
        return GitabaseDescNetwork(
            gbname = gbname,
            gbalias = gbalias,
            gblang = gblang,
            lastModified = lastModified
        )
    }
}
