package com.karate.kime.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.karate.kime.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(vm: MainViewModel, navController: NavHostController) {
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val snackbarHost = remember { SnackbarHostState() }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHost) }) { inner ->
        Column(
            Modifier
                .padding(inner)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Entrar", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Senha") }, singleLine = true, visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation())
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    vm.signIn(email.trim(), password) { ok, err ->
                        scope.launch {
                            if (ok) {
                                snackbarHost.showSnackbar("Login efetuado")
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = false }
                                }
                            } else {
                                snackbarHost.showSnackbar("Erro: ${err ?: "desconhecido"}")
                            }
                        }
                    }
                }, enabled = email.isNotBlank() && password.isNotBlank()) {
                    Text("Entrar")
                }

                OutlinedButton(onClick = { navController.navigate("register") }) {
                    Text("Cadastrar")
                }
            }
        }
    }
}
