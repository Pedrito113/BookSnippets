package com.project.booksnippets

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.unit.dp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.project.booksnippets.data.BookModel
import com.project.booksnippets.data.DataProvider
import com.project.booksnippets.ui.data.UserState

fun <T> SnapshotStateList<T>.swapList(newList: MutableList<T>){
    clear()
    addAll(newList)
}

//fun BookHomeContent(navigateToProfile: (Book) -> Unit) {
@Composable
fun BookHomeContent(onRowClick: (String) -> Unit) {
    val storage = Firebase.storage
    var storageRef = storage.reference

    val vm = UserState.current
    Log.d("USER", vm.bookListTotal.toString())


    val books = remember { mutableStateListOf<BookModel>() }
    val bookes: SnapshotStateList<BookModel> = vm.bookListTotal

//    books.swapList(vm.bookListTotal)
//    books = vm.bookListTotal
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(
            items = books,
            itemContent = {
                BookListItem(book = it, onRowClick)
            }
        )
    }
}