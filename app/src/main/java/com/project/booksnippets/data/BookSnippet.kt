package com.project.booksnippets.data

import android.graphics.Bitmap
import java.io.Serializable

data class BookSnippet(
    val page: Int = 0,
    val bookSnippetImageId: Bitmap
) : Serializable