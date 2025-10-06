package com.gbr.model.gitabase

class GitabaseType(val value: String) {
    companion object {
        val TEXTS = GitabaseType("texts")
        val HELP = GitabaseType("help")
        val MY_BOOKS = GitabaseType("my-books")
        val SHOP = GitabaseType("shop")
    }
}

class GitabaseLang(val value: String) {
    companion object {
        val ENG = GitabaseLang("eng")
        val RUS = GitabaseLang("rus")
    }
}
