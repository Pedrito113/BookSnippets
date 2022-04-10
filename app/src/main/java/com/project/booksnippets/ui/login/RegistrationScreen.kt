package com.project.booksnippets.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.booksnippets.network.models.User
import com.project.booksnippets.ui.data.UserState
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun RegistrationBody(
    onClickRegister: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .semantics { contentDescription = "Registration Screen" }
    ) {
        Spacer(Modifier.height(DefaultPadding))
        RegistrationScreen {
            onClickRegister()
        }
        Spacer(Modifier.height(DefaultPadding))
    }
}

@Composable
fun RegistrationScreen(onClickRegister: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var password_again by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
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
            Text("Registration Screen", fontSize = 32.sp)
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
            TextField(
                value = password_again,
                onValueChange = { password_again = it },
                label = { Text("Enter password again") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button( onClick = {
                //  coroutineScope.launch {
                if (password == password_again) {
                    val uuidStr = UUID.nameUUIDFromBytes(email.toByteArray()).toString()
                    val newUser = User(uuid = uuidStr, email = email, password = password)
                    vm.registration(email, newUser)
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(message = "Passwords are not the same..")
                    }
                }
            }) {
                Text(text = "Register")
            }
            SnackbarHost(
                hostState = snackbarHostState,
            )
        }
    }
}

private val DefaultPadding = 12.dp