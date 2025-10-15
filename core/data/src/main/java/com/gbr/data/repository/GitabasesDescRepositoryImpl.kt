package com.gbr.data.repository

import com.gbr.data.model.GitabaseDesc
import com.gbr.data.model.GitabasesDescResponse
import com.gbr.network.IGitabasesDescDataSource
import com.gbr.network.model.NetworkGitabasesDescResp
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitabasesDescRepositoryImpl @Inject constructor(
    private val gitabasesDescDataSource: IGitabasesDescDataSource
) : GitabasesDescRepository {

    override suspend fun getGitabasesDesc(): GitabasesDescResponse {
        val networkResponse = gitabasesDescDataSource.getGitabasesDesc()
        return networkResponse.toGitabasesDescResponse()
    }

    private fun NetworkGitabasesDescResp.toGitabasesDescResponse(): GitabasesDescResponse {
        return GitabasesDescResponse(
            gitabases = gitabases.map { it.toGitabaseDesc() },
            success = success,
            message = message
        )
    }

    private fun NetworkGitabasesDescResp.Gitabase.toGitabaseDesc(): GitabaseDesc {
        return GitabaseDesc(
            gbname = gbname,
            gbalias = gbalias,
            gblang = gblang,
            lastModified = lastModified
        )
    }
}
