package com.gbr.network.asset

import java.io.InputStream

fun interface AssetManager {
    fun open(filename: String): InputStream
}