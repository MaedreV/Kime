package com.karate.kime.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.karate.kime.MainViewModel
import com.karate.kime.model.Exame
import java.time.LocalDate
import java.util.*

@Composable
fun ExamesScreen(vm: MainViewModel, nav: NavHostController) {
    val exames by remember { derivedStateOf { vm.exames } }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { /* abrir diálogo de agendar */ }) {
                Icon(Icons.Default.Add, contentDescription = "Agendar exame")
            }
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).padding(16.dp)) {
            if (exames.isEmpty()) {
                item {
                    Text("Você ainda não agendou nenhum exame.", Modifier.padding(16.dp))
                }
            } else {
                items(exames) { exame ->
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { /* navegar detalhe de exame */ }
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Data: ${exame.data}")
                            Text("Faixa: ${exame.faixa}")
                            Text("Status: ${exame.status.name}")
                        }
                    }
                }
            }
        }
    }
}