package com.project.booksnippets.ui.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.datastore.preferences.core.intPreferencesKey
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
import androidx.annotation.NonNull
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.google.firebase.database.DatabaseError
import java.util.*


class UserStateViewModel : ViewModel() {
    private lateinit var database: DatabaseReference
    var currentUser: User? = null
    var bookListTotal = mutableStateListOf<BookModel>()
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
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    return true
                }
            }
        }
        return false
    }

    fun signIn(email: String, password: String, snackBar: SnackbarHostState?) {
        isBusy = true

        val uuid = UUID.nameUUIDFromBytes(email.toByteArray()).toString()
        database = FirebaseDatabase.getInstance().getReference("users")
        database.child(uuid).get().addOnSuccessListener {
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
                                bookList.add(book)
                            }
                        }

                        bookListTotal = bookList
                        isBusy = false
                        isLoggedIn = true
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        isBusy = false
                        isLoggedIn = true
                    }
                }
                booksRef!!.addValueEventListener(valueEventListener)
            } else {
                viewModelScope.launch {
                    snackBar?.showSnackbar(message = "Credentials are not correct.")
                }
                isBusy = false
            }
        }.addOnFailureListener{
        }

        Log.d("USER_LOADED", currentUser.toString())
    }

   fun registration(email: String, user: User) {
        isBusy = true

        database = Firebase.database.reference
        database.child("users").child(user.uuid.toString()).setValue(user)
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
