package com.gbr.data

import com.gbr.model.Book
import kotlinx.coroutines.flow.Flow

interface ShopBooksRepository {
    fun getBooks(lang: String): Flow<List<Book>>
    fun getBookDetail(lang: String, bookId: String): Flow<Book>
}
