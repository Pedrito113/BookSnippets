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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.project.booksnippets.data.BookModel
import com.project.booksnippets.data.BookSnippet
import com.project.booksnippets.data.DataProvider
import com.project.booksnippets.network.models.Book
import com.project.booksnippets.ui.data.BookState
import com.project.booksnippets.ui.data.UserState
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.*

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
    lateinit var database: DatabaseReference
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var downloadUri: String = ""
    val storage = Firebase.storage
    var storageRef = storage.reference
    val user = UserState.current.currentUser
    val vm = BookState.current

    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (vm.isAdding) {
            CircularProgressIndicator()
        } else {
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

        ImagePicker(bitmap)
        Spacer(modifier = Modifier.height(16.dp))
        Button( onClick = {
            vm.isAdding = true
            val uuid = UUID.randomUUID()
            val uuidStr = uuid.toString()

            if (title.isEmpty() || author.isEmpty() || description.isEmpty()) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(message = "Please fill all rows.")
                }
                vm.isAdding = false
            } else {
                if (bitmap.value != null) {
                    val imageRef = storageRef.child("${user!!.uuid}/$uuidStr")
                    val baos = ByteArrayOutputStream()
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
                            Log.d("URL", "FAIL URL")
                            vm.isAdding = false
                        }
                    }

                    uploadTask.addOnFailureListener {
                        // Handle unsuccessful uploads
                        Log.d("UPLOAD", "UNSUCCESSFUL")
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(message = "Unsuccessful upload of image to storage.")
                        }
                        vm.isAdding = false
                    }.addOnSuccessListener { taskSnapshot ->
                        Log.d("UPLOAD", "SUCCESS")

                        val book = Book(
                            uuid = uuidStr,
                            title = title,
                            author = author,
                            description = description,
                            status = status,
                            uri = downloadUri,
                            bookSnippets = null
                        )

                        database = Firebase.database.reference
                        user?.uuid?.let { userId ->
                            database.child("books").child(userId).child(uuidStr).setValue(book)
                                .addOnSuccessListener {
                                    Log.d("DB", "Book saved to database")
                                    vm.isAdding = false
                                }.addOnFailureListener {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(message = "Unsuccessful upload of data to database.")
                                    }
                                    vm.isAdding = false
                                }
                        }
                        onAddClick()
                    }
                } else {
                    val book = Book(
                        uuid = uuidStr,
                        title = title,
                        author = author,
                        description = description,
                        status = status,
                        uri = "https://firebasestorage.googleapis.com/v0/b/book-snippets-bf203.appspot.com/o/example?alt=media&token=b6ecf3d5-74dd-4217-be96-485b04d2f48f",
                        bookSnippets = null
                    )

                    database = Firebase.database.reference
                    user?.uuid?.let { userId ->
                        database.child("books").child(userId).child(uuidStr).setValue(book)
                            .addOnSuccessListener {
                                Log.d("DB", "Book saved to database")
                                vm.isAdding = false
                            }.addOnFailureListener {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(message = "Unsuccessful upload of data to database.")
                                }
                                vm.isAdding = false
                            }
                    }
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