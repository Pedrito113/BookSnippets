package com.project.booksnippets

enum class BookSnippetsScreen {
    Login,
    Logout,
    BookHome,
    BookAdd,
    BookEdit,
    SnippetAdd,
    Scanner,
    Registration;

    companion object {
        fun fromRoute(route: String?): BookSnippetsScreen =
            when (route?.substringBefore("/")) {
                BookHome.name -> BookHome
                BookAdd.name -> BookAdd
                Scanner.name -> Scanner
                Login.name -> Login
                Logout.name -> Logout
                SnippetAdd.name -> SnippetAdd
                Registration.name -> Registration
                BookEdit.name -> BookEdit
                null -> Login
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }
    }
}