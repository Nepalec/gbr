package com.gbr.model.shop

data class Chapter(
    val number: Int,
    val title: String,
    val texts: List<ContentsItem>?
)

data class ContentsItem(
    val id: String,
    val number: String,
    val page: Int, //page number
    val title: String,
    val indent: Int = 0,
)
