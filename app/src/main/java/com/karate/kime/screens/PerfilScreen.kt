package com.karate.kime.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PerfilScreen() {
    var dicaAtiva by remember { mutableStateOf(true) }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Usuário: Raul", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text("Receber dica diária")
            Switch(dicaAtiva, onCheckedChange = { dicaAtiva = it })
        }
    }
}