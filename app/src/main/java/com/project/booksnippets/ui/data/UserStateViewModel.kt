package com.project.booksnippets.ui.data

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.project.booksnippets.data.BookModel
import com.project.booksnippets.network.models.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.google.firebase.database.DatabaseError


class UserStateViewModel : ViewModel() {
    private lateinit var database: DatabaseReference
    var currentUser: User? = null
    var bookListTotal = mutableStateListOf<BookModel>()
    //    val storage = Firebase.storage
//    var storageRef = storage.reference
    var bitmap = mutableStateOf<Bitmap?>(null)

    var isLoggedIn by mutableStateOf(false)
    var isBusy by mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.M)
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    fun signIn(email: String, password: String, snackBar: SnackbarHostState?) {
        isBusy = true

        database = FirebaseDatabase.getInstance().getReference("users")
        database.child(email).get().addOnSuccessListener {
            if (it.child("password").value == password) {
                currentUser = it.getValue<User>()

                database = FirebaseDatabase.getInstance().getReference("books")
                val booksRef = currentUser!!.uuid?.let { it1 -> database.child(it1) }
                val valueEventListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val bookList = mutableStateListOf<BookModel>()
                        for (ds in dataSnapshot.children) {
                            var book: BookModel? = ds.getValue<BookModel>()
                            if (book != null) {
                                Log.d("next BOOK", book.toString())
                                bookList.add(book)
                            }
                        }

                        bookListTotal = bookList
                        isBusy = false
                        isLoggedIn = true
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.d("CANCELED", databaseError.message)
                        isBusy = false
                        isLoggedIn = true
                    }
                }
                booksRef!!.addValueEventListener(valueEventListener)
            } else {
                Log.d("wrong_pass", "Wrong password")
                viewModelScope.launch {
                    snackBar?.showSnackbar(message = "Credentials are not correct.")
                }
                isBusy = false
            }
        }.addOnFailureListener{
            Log.d("firebase", "Error getting data", it)
        }

        Log.d("USER_LOADED", currentUser.toString())
    }

    suspend fun registration(email: String, user: User) {
        isBusy = true

        database = Firebase.database.reference
        database.child("users").child(email).setValue(user)
        currentUser = user
        signIn(email = email, user.password!!, null)

        isBusy = false
    }

    suspend fun signOut() {
        isBusy = true
        delay(200)
        isLoggedIn = false
        isBusy = false
    }
}

val UserState = compositionLocalOf<UserStateViewModel> { error("User State Context Not Found!") }
