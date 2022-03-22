package com.project.booksnippets.data

import java.io.Serializable

data class BookSnippet(
    val page: Int = 0,
    val bookSnippetImageId: Int = 0
) : Serializable