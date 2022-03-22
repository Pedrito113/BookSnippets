package com.project.booksnippets.data

import java.io.Serializable

data class Book (
    val id: Int,
    val title: String,
    val author: String,
    val description: String,
    val status: String,
    val bookImageId: Int = 0,
    val bookSnippets: MutableList<BookSnippet>?
) : Serializable