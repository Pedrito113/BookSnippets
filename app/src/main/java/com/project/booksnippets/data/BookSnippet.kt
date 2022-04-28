package com.project.booksnippets.data

import android.graphics.Bitmap
import java.io.Serializable

data class BookSnippet(
    var page: String? = "",
    var keyword: String? = "",
    var uri: String? = "",
    var bookSnippetImageId: Bitmap? = null,
) : Serializable
