package com.gbr.data.repository

import android.util.Log
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
    private val gitabasesCacheDataSource: GitabasesCacheDataSource
) : GitabasesDescRepository {

    companion object {
        private const val TAG = "GitabasesDescRepository"
    }

    override suspend fun getGitabasesDesc(): GitabasesDescResponse {
        return try {
            // Try to get fresh data from network
            val networkResponse = gitabasesDescDataSource.getGitabasesDesc()
            
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
                    message = "Data from cache"
                )
            } else {
                Log.e(TAG, "No cached data available")
                GitabasesDescResponse(
                    gitabases = emptyList(),
                    success = 0,
                    message = "Network error and no cache available: ${e.message}"
                )
            }
        }
    }

    override suspend fun getAllGitabases(): List<Gitabase> {
        val descResponse = getGitabasesDesc()
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
