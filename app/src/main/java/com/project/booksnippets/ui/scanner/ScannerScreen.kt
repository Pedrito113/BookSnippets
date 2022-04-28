package com.project.booksnippets.ui.scanner

import android.Manifest
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import com.project.booksnippets.network.api.BookInfoApi
import com.project.booksnippets.network.models.Book
import com.project.booksnippets.ui.data.UserState
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@ExperimentalPermissionsApi
@Composable
fun ScannerScreen(onScanComplete: () -> Unit = {}) {
    Surface(color = MaterialTheme.colors.background) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            val cameraPermissionState =
                rememberPermissionState(permission = Manifest.permission.CAMERA)

            Button(
                onClick = {
                    cameraPermissionState.launchPermissionRequest()
                }
            ) {
                Text(text = "Camera Permission")
            }

            Spacer(modifier = Modifier.height(10.dp))

            CameraPreview(onScanComplete)
        }
    }
}

@Composable
fun CameraPreview(onScanComplete: () -> Unit = {}) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var preview by remember { mutableStateOf<Preview?>(null) }
    val barCodeVal = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val user = UserState.current.currentUser
    lateinit var database: DatabaseReference
    val snackbarHostState = remember { SnackbarHostState() }

    AndroidView(
        factory = { AndroidViewContext ->
            PreviewView(AndroidViewContext).apply {
                this.scaleType = PreviewView.ScaleType.FILL_CENTER
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            }
        },
        modifier = Modifier
            .fillMaxSize(),
        update = { previewView ->
            val cameraSelector: CameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
            val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
            val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
                ProcessCameraProvider.getInstance(context)

            cameraProviderFuture.addListener({
                preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                val barcodeAnalyser = BarCodeAnalyser { barcodes ->
                    barcodes.forEach { barcode ->
                        barcode.rawValue?.let { barcodeValue ->
                            barCodeVal.value = barcodeValue
                            //Toast.makeText(context, "Succes", Toast.LENGTH_SHORT).show()
                            Toast.makeText(context, barcodeValue, Toast.LENGTH_SHORT).show()
                            val builder = OkHttpClient.Builder()
                            val logging = HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) }
                            builder.addInterceptor(logging)
                            val client = builder.build()

                            val api = Retrofit.Builder()
                                .baseUrl(BookInfoApi.BASE_URL)
                                .client(client)
                                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create()))
                                .build().create(BookInfoApi::class.java)

                            val res = coroutineScope.launch {
                                try {
                                    Log.d("ISBN", barcodeValue)
                                    val types: Map<String, String> = mapOf("q" to "isbn:${barcodeValue}")
                                    val googleBook = api.evaluation(types)
                                    if (googleBook.totalItems > 0) {
                                        var volumeInfo = googleBook.items[0].volumeInfo
                                        volumeInfo.title = volumeInfo.title.replace(".", "")
                                        volumeInfo.title = volumeInfo.title.replace("#", "")
                                        volumeInfo.title = volumeInfo.title.replace("$", "")
                                        volumeInfo.title = volumeInfo.title.replace("(", "")
                                        volumeInfo.title = volumeInfo.title.replace(")", "")
                                        volumeInfo.title = volumeInfo.title.replace("[", "")
                                        volumeInfo.title = volumeInfo.title.replace("]", "")

                                        val uuid = UUID.randomUUID()
                                        val uuidStr = uuid.toString()

                                        val book = Book(
                                            uuid = uuidStr,
                                            title = volumeInfo.title,
                                            author = volumeInfo.authors[0],
                                            description = volumeInfo.description,
                                            status = "Not read",
                                            uri = "https://firebasestorage.googleapis.com/v0/b/book-snippets-bf203.appspot.com/o/example?alt=media&token=b6ecf3d5-74dd-4217-be96-485b04d2f48f",
                                        )

                                        Log.d("Book", book.toString())

                                        database = Firebase.database.reference
                                        database.child("books").child(user?.uuid!!).child(uuidStr).setValue(book)
                                            .addOnSuccessListener {
                                                Log.d("BOOK_ASS", "SAVED")
                                            }.addOnFailureListener {
                                                coroutineScope.launch {
                                                    snackbarHostState.showSnackbar(message = "Unsuccessful upload of data to database.")
                                                }
                                            }


                                        onScanComplete()
                                    }

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                }
                val imageAnalysis: ImageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, barcodeAnalyser)
                    }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    Log.d("TAG", "CameraPreview: ${e.localizedMessage}")
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}