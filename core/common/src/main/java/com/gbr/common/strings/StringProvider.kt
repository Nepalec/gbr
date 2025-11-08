package com.gbr.common.strings

/**
 * Provider for string resources that can be used in ViewModels.
 * This allows ViewModels to access string resources without requiring Android Context directly.
 */
interface StringProvider {
    fun getString(resId: Int): String
    fun getString(resId: Int, vararg formatArgs: Any): String
}


