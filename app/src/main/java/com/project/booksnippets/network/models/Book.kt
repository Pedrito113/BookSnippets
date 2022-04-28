package com.project.booksnippets.network.models

data class Book(
    var uuid: String? = "",
    var title: String? = "",
    var author: String? = "",
    var description: String? = "",
    var status: String? = "",
    val uri: String? = "",
    val currentPage: String? = "",
    val pagesCount: String? = "",
//    val bookImageId: Bitmap? = null,
    var bookSnippets: MutableList<BookSnippet>? = null
)
