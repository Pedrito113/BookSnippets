package com.project.booksnippets

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.booksnippets.data.Book
import com.project.booksnippets.data.DataProvider
import com.project.booksnippets.ui.theme.graySurface

//fun BookListItem(book: Book, navigateToProfile: (Book) -> Unit) {
@Composable
fun BookListItem(book: Book, onRowClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth(),
        elevation = 2.dp,
        shape = RoundedCornerShape(corner = CornerSize(16.dp)),
    ) {
//        Row(Modifier.clickable { navigateToProfile(book) }) {
        Row(Modifier.clickable { onRowClick(book.title) }) {
            BookImage(book)
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
            ) {
                Text(text = book.title, style = typography.h6)
                Text(text = book.author, style = typography.caption)
                Text(text = book.status, style = typography.caption)
            }
        }
    }
}

@Composable
private fun BookImage(book: Book) {
    Image(
        painter = painterResource(id = book.bookImageId),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .padding(8.dp)
            .size(84.dp)
            .clip(RoundedCornerShape(corner = CornerSize(16.dp)))
    )
}

@Preview
@Composable
fun PreviewBookItem() {
    val book = DataProvider.bookList[0]
//    BookListItem(book = book)
}