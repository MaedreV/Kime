package com.karate.kime.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.karate.kime.MainViewModel

import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.filled.ChevronRight



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheFaixasScreen(
    vm: MainViewModel,
    faixaId: String,
    navController: NavHostController
) {
    val req = vm.getRequisitos(faixaId)
    if (req == null) {
        Text("Faixa não encontrada", modifier = Modifier.padding(16.dp))
        return
    }

    // estados para checkboxes
    val estadoKihon = remember { mutableStateMapOf<String, Boolean>() }
    val estadoKata  = remember { mutableStateMapOf<String, Boolean>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Requisitos do Exame") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = req.cor.copy(alpha = 0.8f),
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                "Complete todos os requisitos abaixo",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(req.cor.copy(alpha = 0.1f))
                    .padding(12.dp)
            )
            Spacer(Modifier.height(12.dp))

            // Seção Kihon
            Text(
                "🥋 Kihon",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp)
            )
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(8.dp)) {
                    req.kihon.forEach { golpe ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp) .clickable {
                                    navController.navigate("kihon/detalhe/$golpe")
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val checked = estadoKihon[golpe] ?: false
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { estadoKihon[golpe] = it }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(golpe, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Seção Kata
            Text(
                "📖 Kata",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp)
            )
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(8.dp)) {
                    req.kata.forEach { kata ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { navController.navigate("kata/detalhe/$kata") },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val checked = estadoKata[kata] ?: false
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { estadoKata[kata] = it }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(kata, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.weight(1f))
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = "Ver detalhes do kata",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Botão Confirmar
            Button(
                onClick = { /* salvar e retornar */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                enabled = estadoKihon.values.all { it } && estadoKata.values.all { it }
            ) {
                Text("Confirmar Agendamento")
            }
        }
    }
}
