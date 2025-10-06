package com.gbr.model.gitabase

data class GitabaseID(val type: GitabaseType, val lang:GitabaseLang) {
    val key: String
        get() = "${type.value}_${lang.value}"
}
