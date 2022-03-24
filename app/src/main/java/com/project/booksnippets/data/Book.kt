package com.project.booksnippets.data

import android.graphics.Bitmap
import java.io.Serializable

data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val description: String,
    val status: String,
    val bookImageId: Bitmap,
    val bookSnippets: MutableList<BookSnippet>?
) : Serializable