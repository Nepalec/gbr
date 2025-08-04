package com.gbr.network.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkShopContentResp(
    @SerialName("authors")
    val authors: List<Author>,
    @SerialName("books")
    val books: List<Book>,
    @SerialName("categories")
    val categories: List<Category>,
    @SerialName("isOpen")
    val isOpen: String,
    @SerialName("msg")
    val msg: String
) {
    @Serializable
    data class Author(
        @SerialName("books")
        val books: List<Book>,
        @SerialName("id")
        val id: String,
        @SerialName("Title")
        val title: String
    ) {
        @Serializable
        data class Book(
            @SerialName("uid")
            val uid: String
        )
    }

    @Serializable
    data class Book(
        @SerialName("author")
        val author: String,
        @SerialName("cats")
        val cats: List<Cat>,
        @SerialName("cover_image")
        val coverImage: String,
        @SerialName("currency")
        val currency: String,
        @SerialName("editors_choice")
        val editorsChoice: String,
        @SerialName("exclusive")
        val exclusive: String,
        @SerialName("hasPreview")
        val hasPreview: String,
        @SerialName("hasPurport")
        val hasPurport: String,
        @SerialName("hasSanskrit")
        val hasSanskrit: String,
        @SerialName("_id")
        val id: String,
        @SerialName("isSimple")
        val isSimple: String,
        @SerialName("levels")
        val levels: String,
        @SerialName("old_price")
        val oldPrice: String,
        @SerialName("price")
        val price: String,
        @SerialName("purport_size")
        val purportSize: String,
        @SerialName("text_size")
        val textSize: String,
        @SerialName("title")
        val title: String,
        @SerialName("type")
        val type: String,
        @SerialName("uid")
        val uid: String,
        @SerialName("web_abbrev")
        val webAbbrev: String
    ) {
        @Serializable
        data class Cat(
            @SerialName("id")
            val id: String,
            @SerialName("title")
            val title: String
        )
    }

    @Serializable
    data class Category(
        @SerialName("books")
        val books: List<Book>,
        @SerialName("id")
        val id: String,
        @SerialName("Title")
        val title: String
    ) {
        @Serializable
        data class Book(
            @SerialName("desc")
            val desc: String,
            @SerialName("uid")
            val uid: String
        )
    }
}
