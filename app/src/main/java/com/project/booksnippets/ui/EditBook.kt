package com.project.booksnippets.ui

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.project.booksnippets.data.BookModel
//import com.project.booksnippets.data.Book
//import com.project.booksnippets.data.BookSnippet
import com.project.booksnippets.network.models.Book
import com.project.booksnippets.ui.data.BookState
import com.project.booksnippets.ui.data.UserState
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun EditBook(book: BookModel?, onEditClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .semantics { contentDescription = "Add Book" }
    ) {
        Spacer(Modifier.height(DefaultPadding))
        EditBookScreen(book, onEditClick)
        Spacer(Modifier.height(DefaultPadding))
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun EditBookScreen(book: BookModel?, onEditClick: () -> Unit = {}) {
    lateinit var database: DatabaseReference

    var title by remember { mutableStateOf(book?.title.toString()) }
    var author by remember { mutableStateOf(book?.author.toString()) }
    var description by remember { mutableStateOf(book?.description.toString()) }
    var currentPage by remember { mutableStateOf(book?.currentPage.toString()) }
    var pages by remember { mutableStateOf(book?.pagesCount.toString()) }
    var status by remember { mutableStateOf(book?.status.toString()) }
    var bitmap = remember { mutableStateOf<Bitmap?>(book?.bookImageId) }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var downloadUri: String = ""
    val storage = Firebase.storage
    var storageRef = storage.reference
    val user = UserState.current.currentUser
    val vm = BookState.current
    val statusList = listOf(
        "Not Read",
        "Already Read",
    )
    val isOpen = remember { mutableStateOf(false) } // initial value
    val openCloseOfDropDownList: (Boolean) -> Unit = {
        isOpen.value = it
    }
    val userSelectedString: (String) -> Unit = {
        status = it
    }

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
            Text("Edit Book Screen", fontSize = 32.sp)
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
                value = currentPage,
                onValueChange = { currentPage = it },
                label = { Text("Current Page") },
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = pages,
                onValueChange = { pages = it },
                label = { Text("Pages Count") },
            )
            Spacer(modifier = Modifier.height(16.dp))

            Box {
                Column {
                    OutlinedTextField(
                        value = status,
                        onValueChange = { status = it },
                        label = { Text(text = "Status") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    DropDownList(
                        requestToOpen = isOpen.value,
                        list = statusList,
                        openCloseOfDropDownList,
                        userSelectedString
                    )
                }
                Spacer(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Transparent)
                        .padding(10.dp)
                        .clickable(
                            onClick = { isOpen.value = true }
                        )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            EditImage(bitmap, book)
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                vm.isAdding = true

                if (title.isEmpty() || author.isEmpty() || description.isEmpty()) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(message = "Please fill all rows.")
                    }
                    vm.isAdding = false
                } else if (bitmap.value != null) {
                    val imageRef = storageRef.child("${user!!.uuid}/${book!!.uuid}")
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
                            uuid = book?.uuid,
                            title = title,
                            author = author,
                            description = description,
                            status = status,
                            currentPage = currentPage,
                            pagesCount = pages,
                            uri = downloadUri,
                            bookSnippets = null
                        )

                        database = Firebase.database.reference
                        user!!.uuid?.let {
                            database.child("books").child(it).child(book.uuid.toString()).child("title").setValue(book.title)
                            database.child("books").child(it).child(book.uuid.toString()).child("author").setValue(book.author)
                            database.child("books").child(it).child(book.uuid.toString()).child("description").setValue(book.description)
                            database.child("books").child(it).child(book.uuid.toString()).child("currentPage").setValue(book.currentPage)
                            database.child("books").child(it).child(book.uuid.toString()).child("pagesCount").setValue(book.pagesCount)
                            database.child("books").child(it).child(book.uuid.toString()).child("status").setValue(book.status)
                            database.child("books").child(it).child(book.uuid.toString()).child("uri").setValue(book.uri)
                        }
                        vm.isAdding = false
                        onEditClick()
                        // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                        // ...
                    }
                } else {
                    val book = Book(
                        uuid = book?.uuid,
                        title = title,
                        author = author,
                        description = description,
                        status = status,
                        currentPage = currentPage,
                        pagesCount = pages,
                        uri = downloadUri,
                        bookSnippets = null
                    )
                    database = Firebase.database.reference
                    user!!.uuid?.let {
                        database.child("books").child(it).child(book.uuid.toString()).child("title").setValue(book.title)
                        database.child("books").child(it).child(book.uuid.toString()).child("author").setValue(book.author)
                        database.child("books").child(it).child(book.uuid.toString()).child("description").setValue(book.description)
                        database.child("books").child(it).child(book.uuid.toString()).child("currentPage").setValue(book.currentPage)
                        database.child("books").child(it).child(book.uuid.toString()).child("pagesCount").setValue(book.pagesCount)
                        database.child("books").child(it).child(book.uuid.toString()).child("status").setValue(book.status)
                    }
                    vm.isAdding = false
                    onEditClick()
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
fun EditImage(bitmap: MutableState<Bitmap?>, book: BookModel?) {
    var imageUrl by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    bitmap.value = book?.bookImageId

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

            if (imageUrl == null) {
                Image(
                    painter = rememberAsyncImagePainter(book?.uri),
                    contentDescription = "Gallery Image",
                    modifier = Modifier.size(200.dp)
                )
            } else {
                if (Build.VERSION.SDK_INT < 28) {
                    bitmap.value =
                        MediaStore.Images.Media.getBitmap(context.contentResolver, imageUrl)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, imageUrl!!)
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