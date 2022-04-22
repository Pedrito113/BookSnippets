package com.project.booksnippets.ui.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.project.booksnippets.data.BookModel
import com.project.booksnippets.data.BookSnippet
import com.project.booksnippets.network.models.Book
import com.project.booksnippets.network.models.User
import kotlinx.coroutines.delay


class BooksViewModel : ViewModel() {
    private lateinit var database: DatabaseReference
    val storage = Firebase.storage
    var storageRef = storage.reference
    var snippetsTotal = mutableStateListOf<BookSnippet?>()

    var isAdding by mutableStateOf(false)
    var isAddingSnippet by mutableStateOf(false)

    fun getSnippets(bookUuid: String?, uuid: String?) {
        isAdding = true
        database = FirebaseDatabase.getInstance().getReference("books")
        val bookSnippetsRef = database.child(uuid!!).child(bookUuid!!).child("booksnippets")
        database.child(uuid!!).child(bookUuid!!).child("booksnippets").get().addOnSuccessListener {

            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val snippets = mutableStateListOf<BookSnippet?>()
                    for (snippet in it.children) {
                        var bookSnippet: BookSnippet? = snippet.getValue<BookSnippet>()
                        if (bookSnippet != null) {
                            var imageRef = storageRef.child(uuid).child(bookUuid).child(bookSnippet.keyword!!)
                            val ONE_MEGABYTE: Long = 1024 * 1024
                            imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                                bookSnippet.bookSnippetImageId = mutableStateOf<Bitmap?>(BitmapFactory.decodeByteArray(it,  0, it.size)).value
                                    if (snippetsTotal[0] != null)
                                  snippetsTotal[0] = snippetsTotal[0]
                            }.addOnFailureListener {
                                // Handle any errors
                                Log.d("ERROR", "ERROR")
                            }
                            snippets.add(bookSnippet)
                        }
                    }
                    snippetsTotal = snippets
                    isAdding = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("CANCELED", error.message)
                }
            }
            bookSnippetsRef.addValueEventListener(valueEventListener)


        }
    }

    suspend fun addBook() {
        isAdding = true
        delay(200)
        isAdding = false
    }
}

val BookState = compositionLocalOf<BooksViewModel> { error("Book State Context Not Found!") }