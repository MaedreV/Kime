package com.karate.kime.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.karate.kime.MainViewModel
import com.karate.kime.nav.BottomNavBar
import java.time.LocalDate

data class Faixa(val id: String, val nome: String, val cor: Color)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExamesScreen(vm: MainViewModel, nav: NavHostController) {
    val exames = vm.exames
    val faixas = listOf(
        Faixa("branca",   "Faixa Branca",   Color.White),
        Faixa("amarela",  "Faixa Amarela",  Color.Yellow),
        Faixa("vermelha", "Faixa Vermelha", Color.Red),
        Faixa("laranja",  "Faixa Laranja",  Color(0xFFFFA500)),
        Faixa("verde",    "Faixa Verde",    Color.Green),
        Faixa("roxa",     "Faixa Roxa",     Color(0xFF800080)),
        Faixa("marrom",   "Faixa Marrom",   Color(0xFF654321)),
        Faixa("preta",    "Faixa Preta",    Color.Black),
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Exemplo de agendamento automático: exam in 30 days for faixa amarela
                vm.agendarExame(LocalDate.now().plusDays(30), "amarela")
            }) {
                Icon(Icons.Default.Add, contentDescription = "Agendar exame")
            }
        },
        bottomBar = { BottomNavBar(nav) }
    ) { padding ->
        LazyColumn(
            Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            // Se não há exames agendados
            if (exames.isEmpty()) {
                item {
                    Text(
                        "Você ainda não agendou nenhum exame.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                item { Spacer(Modifier.height(24.dp)) }
                item {
                    Text(
                        "Selecione uma faixa para agendar:",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            } else {
                // Lista de exames agendados
                items(exames) { exame ->
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { /* navegar ao detalhe do exame, se existir */ },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Data: ${exame.data}", style = MaterialTheme.typography.bodyMedium)
                            Text("Faixa: ${exame.faixaId.replaceFirstChar { it.uppercase() }}", style = MaterialTheme.typography.bodyMedium)
                            Text("Status: ${exame.status}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                item { Spacer(Modifier.height(24.dp)) }
                item {
                    Text(
                        "Ou selecione uma faixa nova:",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            // Lista de faixas com cor de fundo
            items(faixas) { faixa ->
                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            nav.navigate("exames/faixa/${faixa.id}")
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = faixa.cor.copy(alpha = 0.2f)
                    )
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            faixa.nome,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (faixa.cor == Color.Black) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
