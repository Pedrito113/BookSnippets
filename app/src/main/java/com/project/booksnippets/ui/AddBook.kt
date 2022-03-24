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
fun AddBook(onAddClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .semantics { contentDescription = "Add Book" }
    ) {
        Spacer(Modifier.height(DefaultPadding))
        AddBookScreen(onAddClick)
        Spacer(Modifier.height(DefaultPadding))
    }
}

@Composable
fun AddBookScreen(onAddClick: () -> Unit = {}) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Add Book Screen", fontSize = 32.sp)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Enter title") },
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = author,
            onValueChange = { author = it },
            label = { Text("Enter author") },
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = status,
            onValueChange = { status = it },
            label = { Text("Enter status") },
        )
        Spacer(modifier = Modifier.height(16.dp))
        val bitmap = remember { mutableStateOf<Bitmap?>(null) }
        ImagePicker(bitmap)
        Spacer(modifier = Modifier.height(16.dp))
        Button( onClick = {
            DataProvider.bookList.add(
                Book(
                    id = DataProvider.bookList.size+1,
                    title = title,
                    author = author,
                    description = description,
                    status = status,
                    bookImageId = bitmap.value!!,
                    bookSnippets = mutableListOf<BookSnippet>()
                )
            )
            onAddClick()
        }) {
            Text(text = "Add")
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ImagePicker(bitmap: MutableState<Bitmap?>) {
    var imageUrl by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUrl = uri
    }


//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(50.dp)
//                .background(Purple500),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Text(
//                text = "Pick Gallery Image",
//                color = Color.White,
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Bold
//            )
//        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                launcher.launch("image/*")
            },
        ) {
            Text(
                text = "Add Image",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            imageUrl?.let {
                if (Build.VERSION.SDK_INT < 28) {
                    bitmap.value = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    bitmap.value = ImageDecoder.decodeBitmap(source)
                }

                bitmap.value?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Gallery Image",
                        modifier = Modifier.size(200.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.padding(20.dp))
        }
    }
}

private val DefaultPadding = 12.dp