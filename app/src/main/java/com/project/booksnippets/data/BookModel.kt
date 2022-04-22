package com.project.booksnippets.data

import android.graphics.Bitmap
import java.io.Serializable

data class BookModel(
    var uuid: String? = "",
    var title: String? = "",
    var author: String? = "",
    var description: String? = "",
    var status: String? = "",
    val currentPage: String? = "",
    val pagesCount: String? = "",
    var uri: String? = "",
    var bookImageId: Bitmap? = null,
    val bookSnippets: MutableList<BookSnippet>? = null
) : Serializable