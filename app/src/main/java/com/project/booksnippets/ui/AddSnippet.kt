package com.project.booksnippets.ui

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.booksnippets.R
import com.project.booksnippets.data.Book
import com.project.booksnippets.data.BookSnippet
import com.project.booksnippets.data.DataProvider
import com.project.booksnippets.ui.theme.Purple500


@Composable
fun AddSnippet(
    book: Book,
    onAddClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .semantics { contentDescription = "Add Snippet" }
    ) {
        Spacer(Modifier.height(DefaultPadding))
        AddSnippetScreen(book, onAddClick)
        Spacer(Modifier.height(DefaultPadding))
    }
}

@Composable
fun AddSnippetScreen(book: Book, onAddClick: () -> Unit = {}) {
    var page by remember { mutableStateOf("") }
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Add Snippet Screen", fontSize = 32.sp)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = page,
            onValueChange = { page = it },
            label = { Text("Enter number pages") },
        )
        Spacer(modifier = Modifier.height(16.dp))

        ImagePicker(bitmap)

        Spacer(modifier = Modifier.height(16.dp))

        Button( onClick = {
            book.bookSnippets?.add(
                BookSnippet(
                page = page.toInt(),
                bookSnippetImageId = bitmap.value!!
            )
            )
            onAddClick()
        }) {
            Text(text = "Add")
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

private val DefaultPadding = 12.dp