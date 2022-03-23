package com.project.booksnippets

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
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
import com.project.booksnippets.ui.data.UserState
import com.project.booksnippets.ui.data.UserStateViewModel
import com.project.booksnippets.ui.login.LoginBody
import com.project.booksnippets.ui.navigation.BottomNavItem
import com.project.booksnippets.ui.theme.BookSnippetsTheme


class MainActivity : AppCompatActivity() {
//main home
private val userState by viewModels<UserStateViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CompositionLocalProvider(UserState provides userState) {
                ApplicationSwitcher()
            }
        }
    }
}

@Composable
fun ApplicationSwitcher() {
    val vm = UserState.current
    if (vm.isLoggedIn) {
        Start(BookSnippetsScreen.BookHome.name)
    } else {
        Start(BookSnippetsScreen.Login.name)
    }
}

@Composable
fun Start(startDestination: String) {
    BookSnippetsTheme(darkTheme = true) {
        val allScreens = BookSnippetsScreen.values().toList()
        val navController = rememberNavController()
        val backstackEntry = navController.currentBackStackEntryAsState()
        var currentScreen = BookSnippetsScreen.fromRoute(
            backstackEntry.value?.destination?.route
        )

        Scaffold(bottomBar = { if (currentScreen != BookSnippetsScreen.Login) BottomNavigation(navController = navController) }) { innerPadding ->
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
    androidx.compose.material.BottomNavigation(
        backgroundColor = colorResource(id = R.color.teal_200),
        contentColor = Color.Black
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.title) },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 9.sp
                    )
                },
                selectedContentColor = Color.Black,
                unselectedContentColor = Color.Black.copy(0.4f),
                alwaysShowLabel = true,
                selected = currentRoute == item.screen_route,
                onClick = { navController.navigate(BookSnippetsScreen.BookHome.name) }
            )
        }
    }
}

@Composable
fun BookSnippetsNavHost(navController: NavHostController, modifier: Modifier, startDestination: String) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(BookSnippetsScreen.Login.name) {
            LoginBody(
//                onClickSeeAllAccounts = { navController.navigate(BookHome.name) },
//                onAccountClick = { title -> navController.navigate("${Login.name}/$title") }
            )
        }

        composable(BookSnippetsScreen.BookHome.name) {
            BookHomeContent(
                onRowClick = { title ->
                    navController.navigate("${BookSnippetsScreen.BookHome.name}/$title")
                }
            )
        }

        val bookTitles = BookSnippetsScreen.BookHome.name
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
            ProfileScreen(
                book = book,
            )
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