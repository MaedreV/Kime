package com.karate.kime.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HistoriaScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Linha do Tempo da História do Karatê", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text("1922: Funakoshi chega ao Japão")
        // ... adicionar mais itens
    }
}