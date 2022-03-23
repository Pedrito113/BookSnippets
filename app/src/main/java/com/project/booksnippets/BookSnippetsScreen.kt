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
    BookHome,
    BookSnippets;

    companion object {
        fun fromRoute(route: String?): BookSnippetsScreen =
            when (route?.substringBefore("/")) {
                Login.name -> Login
                BookHome.name -> BookHome
                BookSnippets.name -> BookSnippets
                null -> Login
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }
    }
}