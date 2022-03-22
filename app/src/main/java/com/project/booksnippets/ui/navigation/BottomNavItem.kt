package com.project.booksnippets.ui.navigation

import com.project.booksnippets.R

sealed class BottomNavItem(var title:String, var icon:Int, var screen_route:String){
    object Home : BottomNavItem("Home", R.drawable.ic_home,"home")
    object Scan: BottomNavItem("Scan", R.drawable.ic_scan,"scan_iban")
    object AddBook: BottomNavItem("New Book", R.drawable.ic_book_add,"add_book")
    object LogOut: BottomNavItem("LogOut", R.drawable.ic_logout,"logout")
}