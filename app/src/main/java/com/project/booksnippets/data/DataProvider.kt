package com.project.booksnippets.data

import com.project.booksnippets.R

// data natvrdo - pouze pro kontrolu použitelnosti

object DataProvider {
    val bookList = listOf(
        Book(
            id = 1,
            title = "Book1",
            author = "Nezval",
            description = "Lorem ipsum may be used as a placeholder before the final copy is available.",
            status = "already read",
            bookImageId = R.drawable.book1,
            bookSnippets = mutableListOf(
                BookSnippet(
                    page = 3,
                    bookSnippetImageId = R.drawable.book2,
                ),
                BookSnippet(
                    page = 4,
                    bookSnippetImageId = R.drawable.book3,
                )
            )
        ),
        Book(
            id = 2,
            title = "Book2",
            author = "Nezval",
            description = "Lorem ipsum may be used as a placeholder before the final copy is available.",
            status = "already read",
            bookImageId = R.drawable.book2,
            bookSnippets = mutableListOf(
                BookSnippet(
                    page = 1,
                    bookSnippetImageId = R.drawable.book1
                )
            ),
        ),
        Book(
            id = 3,
            title = "Book3",
            author = "Nezval",
            description = "Lorem ipsum may be used as a placeholder before the final copy is available.",
            status = "already read",
            bookImageId = R.drawable.book3,
            bookSnippets = null,
        ),
        Book(
            id = 4,
            title = "Book4",
            author = "Nezval",
            description = "Lorem ipsum may be used as a placeholder before the final copy is available.",
            status = "already read",
            bookImageId = R.drawable.book4,
            bookSnippets = null,
        ),
        Book(
            id = 5,
            title = "Book5",
            author = "Nezval",
            description = "JLorem ipsum may be used as a placeholder before the final copy is available.",
            status = "already read",
            bookImageId = R.drawable.book1,
            bookSnippets = null,
        ),
        Book(
            id = 6,
            title = "Book26",
            author = "Nezval",
            description = "Lorem ipsum may be used as a placeholder before the final copy is available.",
            status = "already read",
            bookImageId = R.drawable.book2,
            bookSnippets = null,
        ),
        Book(
            id = 7,
            title = "Book7",
            author = "Nezval",
            description = "Lorem ipsum may be used as a placeholder before the final copy is available.",
            status = "already read",
            bookImageId = R.drawable.book3,
            bookSnippets = null,
        ),
        Book(
            id = 8,
            title = "Book8",
            author = "Nezval",
            description = "Lorem ipsum may be used as a placeholder before the final copy is available.",
            status = "already read",
            bookImageId = R.drawable.book4,
            bookSnippets = null,
        ),
        Book(
            id = 9,
            title = "Book9",
            author = "Nezval",
            description = "Lorem ipsum may be used as a placeholder before the final copy is available.",
            status = "already read",
            bookImageId = R.drawable.book1,
            bookSnippets = null,
        ),
        Book(
            id = 10,
            title = "Book10",
            author = "Nezval",
            description = "Lorem ipsum may be used as a placeholder before the final copy is available.",
            status = "already read",
            bookImageId = R.drawable.book2,
            bookSnippets = null,
        ),
        Book(
            id = 11,
            title = "Book11",
            author = "Nezval",
            description = "Lorem ipsum may be used as a placeholder before the final copy is available.",
            status = "already read",
            bookImageId = R.drawable.book3,
            bookSnippets = null,
        ),
        Book(
            id = 12,
            title = "Book12",
            author = "Nezval",
            description = "Lorem ipsum may be used as a placeholder before the final copy is available.",
            status = "already read",
            bookImageId = R.drawable.book4,
            bookSnippets = null,
        ),
    )

    fun getBook(title: String?): Book {
        return bookList.first { it.title == title }
    }
}