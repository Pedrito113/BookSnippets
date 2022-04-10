package com.project.booksnippets.ui.login

import android.os.Build
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsProperties.Password
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.KeyboardType.Companion.Email
import androidx.compose.ui.text.input.KeyboardType.Companion.Password
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.booksnippets.ui.data.UserState
import kotlinx.coroutines.launch
import java.util.*

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun LoginBody(
    onClickSeeAllBooks: () -> Unit = {},
    onClickRegister: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .semantics { contentDescription = "Overview Screen" }
    ) {
        Spacer(Modifier.height(16.dp))
        LoginScreen(onClickSeeAllBooks, onClickRegister)
        Spacer(Modifier.height(16.dp))
    }
}

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun LoginScreen(onClickSeeAll: () -> Unit, onClickRegister: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val vm = UserState.current
    val snackbarHostState = remember { SnackbarHostState() }
    val modifier = Modifier
    val context = LocalContext.current

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
            Text("Login Screen", fontSize = 32.sp)
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Enter email") },
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Enter password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button( onClick = {
                if (vm.isOnline(context)) {
                    coroutineScope.launch {
                        vm.signIn(email, password, snackbarHostState)
                    }
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(message = "Please connect to internet.")
                    }
                }
            }) {
                Text(text = "LogIn")
            }
            Spacer(modifier = Modifier.height(16.dp))
            ClickableText(text = AnnotatedString("Create account."),
                style = TextStyle(
                    color = Color.White,
                    fontSize = 26.sp,
                ),
                onClick = { onClickRegister() }
            )
            SnackbarHost(
                hostState = snackbarHostState,
            )
        }
    }
}