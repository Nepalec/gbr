package com.gbr.network

interface ShopNetworkDataSource {
    suspend fun getShopContents()
}
