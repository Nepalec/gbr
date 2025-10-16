package com.gbr.model.gitabase

data class GitabaseID(val type: GitabaseType, val lang:GitabaseLang) {
    val key: String
        get() = "${type.value}_${lang.value}"
}
data class GitabaseType(val value: String) {
    companion object {
        val TEXTS = GitabaseType("texts")
        val HELP = GitabaseType("help")
        val MY_BOOKS = GitabaseType("my-books")
        val SHOP = GitabaseType("shop")
    }
}

data class GitabaseLang(val value: String) {
    companion object {
        val ENG = GitabaseLang("eng")
        val RUS = GitabaseLang("rus")
    }
}
