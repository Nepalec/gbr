package com.gbr.model.gitabase

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GitabaseID(val type: GitabaseType, val lang: GitabaseLang) : Parcelable {
    val key: String
        get() = "${type.value}_${lang.value}"

    fun getFilePath(gitabaseFolderPath: String): String {
        return "$gitabaseFolderPath/gitabase_$key.db"
    }
}

fun String.parseGitabaseID(): GitabaseID? {
    return try {
        val parts = this.split("_")
        if (parts.size >= 2) {
            GitabaseID(
                type = GitabaseType(parts[0]),
                lang = GitabaseLang(parts[1])
            )
        } else {
            null
        }
    } catch (e: Exception) {
        null
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