package com.project.booksnippets

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Call
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.project.booksnippets.AnimatingFabContent
import com.project.booksnippets.data.Book
import com.project.booksnippets.data.DataProvider
import com.project.booksnippets.ui.theme.Purple500
import com.project.booksnippets.ui.theme.graySurface

@Composable
fun ProfileScreen(book: Book, onAddClick: (String) -> Unit = {}) {
    val scrollState = rememberScrollState()

    Column(modifier = Modifier.fillMaxSize()) {
        BoxWithConstraints(modifier = Modifier.weight(1f)) {
            Surface {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                ) {
                    ProfileHeader(
                        scrollState,
                        book,
                        this@BoxWithConstraints.maxHeight
                    )
                    ProfileContent(book, this@BoxWithConstraints.maxHeight)

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

@Composable
private fun ProfileHeader(
    scrollState: ScrollState,
    book: Book,
    containerHeight: Dp
) {
    val offset = (scrollState.value / 2)
    val offsetDp = with(LocalDensity.current) { offset.toDp() }

    Image(
        bitmap = book.bookImageId.asImageBitmap(),
        modifier = Modifier
            .heightIn(max = containerHeight / 2)
            .fillMaxWidth()
            .padding(top = offsetDp),
        contentScale = ContentScale.Crop,
        contentDescription = null
    )
}

@Composable
private fun ProfileContent(book: Book, containerHeight: Dp) {
    Column {
        Spacer(modifier = Modifier.height(8.dp))

        Name(book)

        ProfileProperty(label = "Title", value = book.title)
        ProfileProperty(label= "Author", book.author)
        ProfileProperty(label = "Description", value = book.description)

        book.bookSnippets?.forEach {
            SnippetImage(image = it.bookSnippetImageId, containerHeight)
            ProfileProperty(label = "Snippet Page", value = it.page.toString())
        }

        // Add a spacer that always shows part (320.dp) of the fields list regardless of the device,
        // in order to always leave some content at the top.
        Spacer(Modifier.height((containerHeight - 120.dp).coerceAtLeast(0.dp)))
    }
}

@Composable
private fun Name(
    book: Book
) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        Name(
            book = book,
            modifier = Modifier.height(32.dp)
        )
    }
}

@Composable
private fun Name(book: Book, modifier: Modifier = Modifier) {
    Text(
        text = book.title,
        modifier = modifier,
        style = MaterialTheme.typography.h5,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun ProfileProperty(label: String, value: String) {
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
fun AdoptFab(onAddClick: (String) -> Unit, extended: Boolean, modifier: Modifier = Modifier, book: Book) {
    FloatingActionButton(
        onClick = { onAddClick(book.title) },
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

@Preview
@Composable
fun ProfilePreview() {
    val book = DataProvider.bookList[0]
    ProfileScreen(book = book)
}