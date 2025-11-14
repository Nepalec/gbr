package com.gbr.model.book

enum class BookStructure(val levels: Int) {
    TEXTS_INDENTED(1),
    TEXTS(2),
    CHAPTERS(3);

    companion object {
        fun fromInt(value: Int): BookStructure? = BookStructure.entries.firstOrNull { it.levels == value }
    }
}