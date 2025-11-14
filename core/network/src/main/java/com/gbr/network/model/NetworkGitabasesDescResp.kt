package com.gbr.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkGitabasesDescResp(
    @SerialName("gitabases")
    val gitabases: List<Gitabase>,
    @SerialName("success")
    val success: Int,
    @SerialName("message")
    val message: String
) {
    @Serializable
    data class Gitabase(
        @SerialName("gbname")
        val gbname: String,
        @SerialName("gbalias")
        val gbalias: String,
        @SerialName("gblang")
        val gblang: String,
        @SerialName("last_modified")
        val lastModified: String
    )
}