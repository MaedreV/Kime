package com.karate.kime.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.karate.kime.MainViewModel

@Composable
fun PerfilScreen(vm: MainViewModel) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
        Text("Área do Usuário", style = MaterialTheme.typography.titleMedium)
    }
}