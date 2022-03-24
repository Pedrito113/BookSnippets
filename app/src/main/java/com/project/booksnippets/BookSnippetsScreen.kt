package com.project.booksnippets

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.project.booksnippets.ui.login.LoginBody
import com.project.booksnippets.data.DataProvider

enum class BookSnippetsScreen {
    Login,
    Logout,
    BookHome,
    BookAdd,
    SnippetAdd,
    Scanner;

    companion object {
        fun fromRoute(route: String?): BookSnippetsScreen =
            when (route?.substringBefore("/")) {
                BookHome.name -> BookHome
                BookAdd.name -> BookAdd
                Scanner.name -> Scanner
                Login.name -> Login
                Logout.name -> Logout
                SnippetAdd.name -> SnippetAdd
                null -> Login
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }
    }
}