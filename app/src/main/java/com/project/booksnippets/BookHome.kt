package com.project.booksnippets

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.project.booksnippets.data.Book
import com.project.booksnippets.data.DataProvider

//fun BookHomeContent(navigateToProfile: (Book) -> Unit) {
@Composable
fun BookHomeContent(onRowClick: (String) -> Unit) {
    val books = remember { DataProvider.bookList }
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(
            items = books,
            itemContent = {
                BookListItem(book = it, onRowClick)
            }
        )
    }
}