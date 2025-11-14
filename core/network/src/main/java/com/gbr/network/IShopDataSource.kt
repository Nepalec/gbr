package com.gbr.network

import com.gbr.network.model.NetworkShopContentResp

interface IShopDataSource {
    suspend fun getShopContents(lang: String): NetworkShopContentResp
}