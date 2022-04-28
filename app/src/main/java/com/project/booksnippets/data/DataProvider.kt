package com.project.booksnippets.data


object DataProvider {
    val bookList = mutableListOf<BookModel>(
        // Historic redundant code
    )

    fun getBook(title: String?): BookModel {
        return bookList.first { it.title == title }
    }
}