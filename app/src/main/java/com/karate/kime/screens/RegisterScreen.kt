package com.karate.kime.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.karate.kime.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(vm: MainViewModel, navController: NavHostController) {
    val scope = rememberCoroutineScope()
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeat by remember { mutableStateOf("") }
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
            Text("Cadastro", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome") })
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Senha") }, singleLine = true, visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = repeat, onValueChange = { repeat = it }, label = { Text("Repita a senha") }, singleLine = true, visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation())
            Spacer(Modifier.height(16.dp))
            Button(onClick = {
                if (password != repeat) {
                    scope.launch { snackbarHost.showSnackbar("Senhas não conferem") }
                    return@Button
                }
                vm.signUp(nome.trim(), email.trim(), password) { ok, err ->
                    scope.launch {
                        if (ok) {
                            snackbarHost.showSnackbar("Cadastro realizado")
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = false }
                            }
                        } else {
                            snackbarHost.showSnackbar("Erro: ${err ?: "desconhecido"}")
                        }
                    }
                }
            }, enabled = nome.isNotBlank() && email.isNotBlank() && password.isNotBlank() && repeat.isNotBlank()) {
                Text("Cadastrar")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { navController.navigate("login") }) { Text("Voltar ao login") }
        }
    }
}
