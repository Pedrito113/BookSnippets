package com.project.booksnippets

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.google.firebase.storage.ktx.storage
import com.project.booksnippets.data.BookModel
import com.project.booksnippets.data.BookSnippet
import com.project.booksnippets.data.DataProvider
import com.project.booksnippets.ui.data.BookState
import com.project.booksnippets.ui.data.UserState
import com.project.booksnippets.ui.theme.graySurface

@Composable
fun ProfileScreen(book: BookModel?, onAddClick: (String) -> Unit,
    onEditBookClick: (String) -> Unit,
    onRemoveBookClick: () -> Unit) {
        val scrollState = rememberScrollState()
        val vm = BookState.current
        if (vm.isAdding) {
            CircularProgressIndicator()
        } else {
            Log.d("USER UUID", UserState.current.currentUser!!.uuid.toString())

    Column(modifier = Modifier.fillMaxSize()) {
        BoxWithConstraints(modifier = Modifier.weight(1f)) {
            Surface {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    ProfileContent(
                        book,
                        this@BoxWithConstraints.maxHeight,
                        onEditBookClick,
                        onRemoveBookClick
                    )

                }
            }
            AdoptFab(
                onAddClick = onAddClick,
                extended = scrollState.value == 0,
                modifier = Modifier.align(Alignment.BottomEnd),
                book = book
            )
        }
    }
}
}

@Composable
private fun ProfileHeader(
    book: BookModel?,
    containerHeight: Dp,
) {
    Image(
        painter = rememberAsyncImagePainter(book?.uri),
        modifier = Modifier
            .heightIn(max = containerHeight / 2)
            .fillMaxWidth()
            .padding(top = 20.dp),
        contentScale = ContentScale.Crop,
        contentDescription = null
    )
    if (book != null) {
        book.title?.let { ProfileProperty(book = book, label = "Title", value = it) }
        book.author?.let { ProfileProperty(book = book, label= "Author", it) }
        book.description?.let { ProfileProperty(book = book, label = "Description", value = it) }
    }
}

@Composable
private fun ProfileContent(book: BookModel?, containerHeight: Dp, onEditBookClick: (String) -> Unit,
                           onRemoveBookClick: () -> Unit) {
    val vm = BookState.current
    val user = UserState.current.currentUser
    val storage = Firebase.storage
    val storageRef = storage.reference.child(user?.uuid.toString()).child(book?.uuid.toString())
    val subStorageRef = storage.reference.child(user?.uuid.toString()).child(book?.uuid.toString()+"/")
    var database: DatabaseReference = Firebase.database.reference


    Column {
        Spacer(modifier = Modifier.height(8.dp))

        if (book != null) {
            Name(book)
        }
            Button(onClick = { onEditBookClick(book?.title.toString()) }, Modifier.padding(top = 16.dp, start = 16.dp)) {
                Text(text = "Edit")
            }

            Button(onClick = {
                user?.uuid?.let { userId ->
                    database.child("books").child(userId).child(book?.uuid.toString()).removeValue()
                    subStorageRef.listAll().addOnSuccessListener { (items, _) ->
                        items.forEach {
                            it.delete()
                        }
                    }
                    storageRef.delete()
                }
                onRemoveBookClick()

            }, Modifier.padding(top = 16.dp, start = 16.dp)) {
                Text(text = "Remove")
            }

            val snippets: SnapshotStateList<BookSnippet?> = vm.snippetsTotal
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                item{ ProfileHeader(book, containerHeight) }
                items(
                    items = snippets,
                    itemContent = {
                        Image(
                            painter = rememberAsyncImagePainter(it?.uri),
                            modifier = Modifier
                                .heightIn(max = containerHeight / 2)
                                .fillMaxWidth()
                                .padding(top = 20.dp),
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                        )
                        ProfileProperty(book = book, label = "Keyword", value = it?.keyword.toString())
                        Button(onClick = {
                            user?.uuid?.let { userId ->
                                database.child("books").child(userId).child(book?.uuid.toString())
                                    .child("booksnippets").child(it?.keyword.toString()).removeValue()
                                subStorageRef.child(it?.keyword.toString()).delete()
                                onRemoveBookClick()
                            }
                        }) {
                            Text(text = "Remove Snippet")
                        }
                    }
                )
            }
        }
    }

@Composable
private fun Name(
    book: BookModel
) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        Name(
            book = book,
            modifier = Modifier.height(32.dp)
        )
    }
}

@Composable
private fun Name(book: BookModel?, modifier: Modifier = Modifier) {
    if (book != null) {
        book.title?.let {
            Text(
                text = it,
                modifier = modifier,
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ProfileProperty(book: BookModel?, label: String, value: String) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        Divider(modifier = Modifier.padding(bottom = 4.dp))
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = label,
                modifier = Modifier.height(24.dp),
                style = MaterialTheme.typography.caption,
            )
        }

        Text(
            text = value,
            modifier = Modifier.height(24.dp),
            overflow = TextOverflow.Visible
        )
    }
}

@Composable
fun SnippetImage(image: Bitmap, containerHeight: Dp) {
    Image(
        bitmap = image.asImageBitmap(),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = containerHeight / 3)
            .padding(top = 10.dp),
        contentScale = ContentScale.Crop,
        contentDescription = null
    )
}

@Composable
fun AdoptFab(onAddClick: (String) -> Unit, extended: Boolean, modifier: Modifier = Modifier, book: BookModel?) {
    FloatingActionButton(
        onClick = { if (book != null) {
            onAddClick(book.title!!)
           }
        },
        modifier = modifier
            .padding(16.dp)
            .padding()
            .height(48.dp)
            .widthIn(min = 48.dp),
        backgroundColor = White,
        contentColor = graySurface
    ) {
        AnimatingFabContent(
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Add Snippet"
                )
            },
            text = {
                Text(
                    text = "Add Snippet",
                )
            },
            extended = extended
        )
    }
}
