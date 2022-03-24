package com.project.booksnippets

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.project.booksnippets.data.DataProvider
import com.project.booksnippets.ui.AddBook
import com.project.booksnippets.ui.AddSnippet
import com.project.booksnippets.ui.data.UserState
import com.project.booksnippets.ui.data.UserStateViewModel
import com.project.booksnippets.ui.login.LoginBody
import com.project.booksnippets.ui.navigation.BottomNavItem
import com.project.booksnippets.ui.scanner.ScannerScreen
import com.project.booksnippets.ui.theme.BookSnippetsTheme
import kotlinx.coroutines.launch
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.project.booksnippets.BookSnippetsScreen.Login
import com.project.booksnippets.BookSnippetsScreen.BookHome
import com.project.booksnippets.BookSnippetsScreen.Logout
import com.project.booksnippets.BookSnippetsScreen.Scanner
import com.project.booksnippets.BookSnippetsScreen.BookAdd
import com.project.booksnippets.BookSnippetsScreen.SnippetAdd






class MainActivity : ComponentActivity() {
//main home
private val userState by viewModels<UserStateViewModel>()

    @ExperimentalPermissionsApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(UserState provides userState) {
                ApplicationSwitcher()
            }
        }
    }
}

@ExperimentalPermissionsApi
@Composable
fun ApplicationSwitcher() {
    val vm = UserState.current
    if (vm.isLoggedIn) {
        Start(BookHome.name)
    } else {
        Start(Login.name)
    }
}

@ExperimentalPermissionsApi
@Composable
fun Start(startDestination: String) {
    BookSnippetsTheme(darkTheme = true) {
        val allScreens = BookSnippetsScreen.values().toList()
        val navController = rememberNavController()
        val backstackEntry = navController.currentBackStackEntryAsState()
        var currentScreen = BookSnippetsScreen.fromRoute(
            backstackEntry.value?.destination?.route
        )

        Scaffold(bottomBar = { if (currentScreen != Login) BottomNavigation(navController = navController) }) { innerPadding ->
            BookSnippetsNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                startDestination = startDestination
            )
        }
    }
}

@Composable
fun BottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Scan,
        BottomNavItem.AddBook,
        BottomNavItem.LogOut,
    )
    BottomNavigation(
        backgroundColor = colorResource(id = R.color.teal_200),
        contentColor = Color.Black
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.title) },
                label = { Text(text = item.title,
                        fontSize = 9.sp) },
                selectedContentColor = Color.Black,
                unselectedContentColor = Color.Black.copy(0.4f),
                alwaysShowLabel = true,
                selected = currentRoute == item.screen_route,
                onClick = { navController.navigate(item.screen_route) }
            )
        }
    }
}

@ExperimentalPermissionsApi
@Composable
fun BookSnippetsNavHost(navController: NavHostController, modifier: Modifier, startDestination: String) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(Login.name) {
            LoginBody(
//                onClickSeeAllAccounts = { navController.navigate(BookHome.name) },
//                onAccountClick = { title -> navController.navigate("${Login.name}/$title") }
            )
        }

        composable(Logout.name) {
            Logout()
        }

        composable(BookHome.name) {
            BookHomeContent(
                onRowClick = { title ->
                    navController.navigate("${BookHome.name}/$title")
                }
            )
        }

        composable(Scanner.name) {
            ScannerScreen()
        }

        composable(BookAdd.name) {
            AddBook(
                onAddClick = { navController.navigate(BookHome.name) }
            )
        }

        val bookTitles = BookHome.name
        composable(
            route = "$bookTitles/{title}",
            arguments = listOf(
                navArgument("title") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val title = entry.arguments?.getString("title")
            val book = DataProvider.getBook(title)
            Log.d("title", title.toString())
            Log.d("book", title.toString())
            ProfileScreen(
                book = book,
                onAddClick = { title ->
                    navController.navigate("${BookHome.name}/$title/addSnippet")
                }
            )
        }

        val bookTitle = BookHome.name
        composable(
            route = "$bookTitle/{title}/addSnippet",
            arguments = listOf(
                navArgument("title") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val title = entry.arguments?.getString("title")
            val book = DataProvider.getBook(title)
            AddSnippet(
                book = book,
                onAddClick = { navController.navigate(BookHome.name) }
            )
        }
    }
}

@Composable
fun Logout() {
    val composableScope = rememberCoroutineScope()
    val vm = UserState.current
    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (vm.isBusy) {
            CircularProgressIndicator()
        } else {
            composableScope.launch {
                vm.signOut()
            }
        }
    }
}


///////////////////////////////////////////////////////////
//@Composable
//fun MyApp(navigateToProfile: (Book) -> Unit) {
//    Scaffold(
//        content = {
////            BookHomeContent(navigateToProfile = navigateToProfile)
//        }
//    )
//}
//
//@Preview("Light Theme", widthDp = 360, heightDp = 640)
//@Composable
//fun LightPreview() {
//    BookSnippetsTheme(darkTheme = false) {
//        MyApp { }
//    }
//}
//
//@Preview("Dark Theme", widthDp = 360, heightDp = 640)
//@Composable
//fun DarkPreview() {
//    BookSnippetsTheme(darkTheme = true) {
//        MyApp { }
//    }
//}