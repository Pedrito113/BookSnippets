package com.project.booksnippets.ui

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.project.booksnippets.data.BookModel
import com.project.booksnippets.network.models.BookSnippet
import com.project.booksnippets.ui.data.BookState
import com.project.booksnippets.ui.data.UserState
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream



@Composable
fun AddSnippet(
    book: BookModel,
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
fun ImagePickerAndText(bitmap: MutableState<Bitmap?>) {
    var imageUrl by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUrl = uri
    }

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
    //*********************************************************************************************************//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.LightGray)
//            .padding(10.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Button(
//            onClick = {
//                launcher.launch("image/*")
//            },
//        ) {
//            Text(
//                text = "Add TEXT from Image",
//                color = Color.White,
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Bold
//            )
//        }
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color.LightGray)
//                .padding(10.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            imageUrl?.let {
//                if (Build.VERSION.SDK_INT < 28) {
//                    bitmap.value = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
//                } else {
//                    val source = ImageDecoder.createSource(context.contentResolver, it)
//                    bitmap.value = ImageDecoder.decodeBitmap(source)
//                }
//
//                bitmap.value?.let { bitmap ->
//                    Image(
//                        bitmap = bitmap.asImageBitmap(),
//                        contentDescription = "Gallery Image",
//                        modifier = Modifier.size(200.dp)
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.padding(20.dp))
//        }
//    }
//*********************************************************************************************************//

  //  Spacer(modifier = Modifier.padding(20.dp))
}

@Composable
fun AddSnippetScreen(book: BookModel, onAddClick: () -> Unit = {}) {
    var page by remember { mutableStateOf("") }
    var keyword by remember { mutableStateOf("") }
    //var text by remember { mutableStateOf("")}   //*************************************************
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val storage = Firebase.storage
    var storageRef = storage.reference
    val user = UserState.current.currentUser
    lateinit var database: DatabaseReference
    var downloadUri: String = ""
    val vm = BookState.current

    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (vm.isAddingSnippet) {
            CircularProgressIndicator()
        } else {
            Text("Add Snippet Screen", fontSize = 32.sp)
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = keyword,
                onValueChange = { keyword = it },
                label = { Text("Enter keyword") },
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = page,
                onValueChange = { page = it },
                label = { Text("Enter page number") },
            )
//            Spacer(modifier = Modifier.height(16.dp))
//            TextField(
//                value = text,
//                onValueChange = { text = it },
//                label = { Text("-TEXT-") }
//            )

            Spacer(modifier = Modifier.height(16.dp))
            //******************************************************************************************//
            //ImagePickerAndText(bitmap)

            ImagePicker(bitmap)

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                vm.isAdding = true
                val imageRef = storageRef.child("${user!!.uuid}/${book.uuid}/$keyword")
                val baos = ByteArrayOutputStream()

                if (keyword.isEmpty() || page.isEmpty()) {
                    vm.isAdding = false
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(message = "Please fill all rows.")
                    }
                } else if (bitmap.value != null) {
                    val bitmapFirebase = bitmap.value!!
                    bitmapFirebase.compress(Bitmap.CompressFormat.JPEG, 25, baos)
                    val data = baos.toByteArray()

                    var uploadTask = imageRef.putBytes(data).continueWithTask() { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                            vm.isAdding = false
                        }
                        imageRef.downloadUrl
                    }.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            downloadUri = task.result.toString()
                            Log.d("URL", downloadUri.toString())
                        } else {
                            vm.isAdding = false
                            Log.d("URL", "FAIL URL")
                        }
                    }

                    uploadTask.addOnFailureListener {
                        // Handle unsuccessful uploads
                        coroutineScope.launch {
                            vm.isAdding = false
                            snackbarHostState.showSnackbar(message = "Unsuccessful upload of image to storage.")
                        }
                    }.addOnSuccessListener { taskSnapshot ->
                        Log.d("UPLOAD", "SUCCESS")

                        val bookSnippet = BookSnippet(
                            page = page,
                            keyword = keyword,
                            uri = downloadUri,
                        )

                        database = Firebase.database.reference
                        user!!.uuid?.let {
                            database.child("books").child(user.uuid.toString())
                                .child(book.uuid.toString())
                                .child("booksnippets").child(keyword).setValue(bookSnippet)
                                .addOnSuccessListener {
                                    Log.d("BOOK_ASS", "SAVED")
                                }.addOnFailureListener {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(message = "Unsuccessful upload of data to database.")
                                    }
                                    vm.isAdding = false
                                }
                        }
                        vm.isAdding = false
                    }
                    onAddClick()
                } else {
                    val bookSnippet = BookSnippet(
                        page = page,
                        keyword = keyword,
                        uri = "https://firebasestorage.googleapis.com/v0/b/book-snippets-bf203.appspot.com/o/example?alt=media&token=b6ecf3d5-74dd-4217-be96-485b04d2f48f"
                    )

                    if (bookSnippet.page!!.isEmpty() || !bookSnippet.page!!.isDigitsOnly() || bookSnippet.keyword!!.isEmpty()) {
                        vm.isAdding = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(message = "Please make sure all rows are filled and page row is only in digits.")
                        }
                    } else {
                        database = Firebase.database.reference
                        user.uuid?.let {
                            database.child("books").child(user.uuid.toString())
                                .child(book.uuid.toString())
                                .child("booksnippets").child(keyword).setValue(bookSnippet)
                                .addOnSuccessListener {
                                    Log.d("BOOK_ASS", "SAVED")
                                }.addOnFailureListener {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(message = "Unsuccessful upload of data to database.")
                                    }
                                    vm.isAdding = false
                                }
                        }
                        vm.isAdding = false
                        onAddClick()
                    }
                }
            }) {
                Text(text = "Add")
            }
            Spacer(modifier = Modifier.height(32.dp))

            SnackbarHost(
                hostState = snackbarHostState,
            )
        }
    }
}


private val DefaultPadding = 12.dp