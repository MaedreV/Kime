package com.karate.kime.screens

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.karate.kime.MainViewModel
import com.karate.kime.model.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun PerfilScreen(vm: MainViewModel) {
    val currentUser by vm.currentUser.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { inner ->
        Box(modifier = Modifier.fillMaxSize().padding(inner)) {
            if (currentUser == null) {
                AuthForms(vm = vm, onMessage = { msg ->
                    scope.launch {
                        snackbarHostState.showSnackbar(msg)
                    }
                })
            } else {
                LoggedProfile(user = currentUser!!, onSignOut = {
                    vm.signOut()
                })
            }
        }
    }
}


@Composable
private fun LoggedProfile(user: User, onSignOut: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Bem-vindo,", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text(user.nome ?: "(sem nome)", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(4.dp))
        Text(user.email ?: "-", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onSignOut) {
            Text("Sair")
        }
    }
}

@Composable
private fun AuthForms(vm: MainViewModel, onMessage: (String) -> Unit) {
    var showRegister by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // campos registro
    var name by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regRepeat by remember { mutableStateOf("") }

    val activity = LocalContext.current as? Activity

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
        if (!showRegister) {
            Text("Entrar", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-mail") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Senha") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    vm.signIn(email.trim(), password) { ok, err ->
                        if (ok) {
                            onMessage("Login realizado")
                        } else {
                            onMessage("Erro no login: ${err ?: "desconhecido"}")
                        }
                    }
                }, enabled = email.isNotBlank() && password.isNotBlank()) {
                    Text("Login")
                }
                OutlinedButton(onClick = { showRegister = true }) {
                    Text("Cadastrar")
                }
            }
        } else {
            Text("Cadastro", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome completo") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = regEmail,
                onValueChange = { regEmail = it },
                label = { Text("E-mail") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = regPassword,
                onValueChange = { regPassword = it },
                label = { Text("Senha") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = regRepeat,
                onValueChange = { regRepeat = it },
                label = { Text("Repetir senha") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    if (regPassword != regRepeat) {
                        onMessage("Senhas não conferem")
                        return@Button
                    }
                    vm.signUp(name.trim(), regEmail.trim(), regPassword) { ok, err ->
                        if (ok) {
                            onMessage("Cadastro realizado — você será logado automaticamente")
                            // limpa e volta para tela de login (o authstate listener atualiza o perfil)
                            name = ""; regEmail = ""; regPassword = ""; regRepeat = ""
                            showRegister = false
                        } else {
                            onMessage("Erro no cadastro: ${err ?: "desconhecido"}")
                        }
                    }
                }, enabled = name.isNotBlank() && regEmail.isNotBlank() && regPassword.isNotBlank()) {
                    Text("Confirmar cadastro")
                }
                OutlinedButton(onClick = { showRegister = false }) {
                    Text("Voltar")
                }
            }
        }
    }
}
