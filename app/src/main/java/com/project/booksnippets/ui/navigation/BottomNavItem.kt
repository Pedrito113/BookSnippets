package com.project.booksnippets.ui.navigation

import com.project.booksnippets.BookSnippetsScreen
import com.project.booksnippets.R

sealed class BottomNavItem(var title:String, var icon:Int, var screen_route:String){
    object Home : BottomNavItem("Home", R.drawable.ic_home, BookSnippetsScreen.BookHome.name)
    object Scan: BottomNavItem("Scan",R.drawable.ic_scan, BookSnippetsScreen.Scanner.name)
    object AddBook: BottomNavItem("New Book",R.drawable.ic_book_add, BookSnippetsScreen.BookAdd.name)
    object LogOut: BottomNavItem("LogOut",R.drawable.ic_logout,"logout")
}