package com.gbr.model.gitabase

data class Gitabase(
    val id: GitabaseID,
    val title: String,
    val version: Int,
    val filePath: String,
    val isShopDatabase: Boolean = false,
    val hasTranslation: Boolean = false,
    val lastModified: String
) {
    val canDelete: Boolean
        get() = id.type != GitabaseType.HELP && id.type != GitabaseType.MY_BOOKS

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Gitabase) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
