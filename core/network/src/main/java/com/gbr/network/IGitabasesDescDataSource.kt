package com.gbr.network

import com.gbr.network.model.NetworkGitabasesDescResp

interface IGitabasesDescDataSource {
    suspend fun getGitabasesDesc(is4Download: Boolean): NetworkGitabasesDescResp
}