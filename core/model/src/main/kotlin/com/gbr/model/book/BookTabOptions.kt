package com.gbr.model.book

data class BookContentsTabOptions(
    val textSize: Int = 0,
    val columns: Int = 2
)

data class BookImagesTabOptions(
    val columns: Int = 2,
    val groupByChapter: Boolean = true
)