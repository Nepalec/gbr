package com.gbr.model.gitabase

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GitabaseID(val type: GitabaseType, val lang:GitabaseLang) : Parcelable {
    val key: String
        get() = "${type.value}_${lang.value}"

    fun getFilePath(gitabaseFolderPath: String): String {
        return "$gitabaseFolderPath/gitabase_${key}.db"
    }
}
@Parcelize
data class GitabaseType(val value: String) : Parcelable {
    companion object {
        val TEXTS = GitabaseType("texts")
        val HELP = GitabaseType("help")
        val MY_BOOKS = GitabaseType("my-books")
        val SHOP = GitabaseType("shop")
    }
}

@Parcelize
data class GitabaseLang(val value: String) : Parcelable {
    companion object {
        val ENG = GitabaseLang("eng")
        val RUS = GitabaseLang("rus")
    }

}
