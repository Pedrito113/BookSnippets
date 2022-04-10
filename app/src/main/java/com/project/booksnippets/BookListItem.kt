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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.project.booksnippets.data.BookModel
import com.project.booksnippets.data.DataProvider
import com.project.booksnippets.ui.data.BookState
import com.project.booksnippets.ui.data.UserState

//fun BookListItem(book: Book, navigateToProfile: (Book) -> Unit) {
@Composable
fun BookListItem(book: BookModel, onRowClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth(),
        elevation = 2.dp,
        shape = RoundedCornerShape(corner = CornerSize(16.dp)),
    ) {
        val vm = BookState.current
        val user = UserState.current.currentUser
//        Row(Modifier.clickable { navigateToProfile(book) }) {
        Row(Modifier.clickable { book.title?.let {
            vm.getSnippets(book.uuid, user?.uuid)
            onRowClick(it)
        } }) {
//            if (book.bookImageId != null)
            BookImage(book)
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
            ) {
                book.title?.let { Text(text = it, style = typography.h6) }
                book.author?.let { Text(text = it, style = typography.caption) }
                book.status?.let { Text(text = it, style = typography.caption) }
            }
        }
    }
}

@Composable
private fun BookImage(book: BookModel) {
    Image(
        painter = rememberAsyncImagePainter(book.uri),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .padding(8.dp)
            .size(84.dp)
            .clip(RoundedCornerShape(corner = CornerSize(16.dp)))
    )

//    Image(
//        bitmap = book.bookImageId!!.asImageBitmap(),
//        painter = painterResource(id = book.bookImageId),
//        contentDescription = null,
//        contentScale = ContentScale.Crop,
//        modifier = Modifier
//            .padding(8.dp)
//            .size(84.dp)
//            .clip(RoundedCornerShape(corner = CornerSize(16.dp)))
//    )
}

@Preview
@Composable
fun PreviewBookItem() {
    val book = DataProvider.bookList[0]
//    BookListItem(book = book)
}