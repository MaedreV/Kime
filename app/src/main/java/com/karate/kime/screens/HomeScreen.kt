package com.karate.kime.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.karate.kime.MainViewModel
import com.karate.kime.nav.Rota

@Composable
fun HomeScreen(
    vm: MainViewModel,
    nav: NavHostController
) {

    val diasParaExame = 14
    val progressoKihon = 0.6f
    val progressoKata  = 0.3f
 //   val progressoKumite= 0.4f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // card da dica
        Card(
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Dica do Dia",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Respiração no Karatê: sincronize sua respiração com seus movimentos para maximizar a potência e o controle.",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "Ver Mais ›",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable { /* abrir modal da dica mais tarde */ }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // progresso geral
        Text(
            text = "Progresso Geral",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(8.dp))

        // linha de progresso
        @Composable
        fun ProgressoLinha(progress: Float, label: String) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(MaterialTheme.shapes.small),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )
                Spacer(Modifier.width(8.dp))
                Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.bodySmall)
            }
        }

        ProgressoLinha(progressoKihon, "Kihon")
        Spacer(Modifier.height(8.dp))
        ProgressoLinha(progressoKata, "Kata")
        Spacer(Modifier.height(8.dp))
       // ProgressoLinha(progressoKumite, "Kumite")

        Spacer(Modifier.height(24.dp))

        // prox exame se agendado
        Card(
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { nav.navigate(Rota.Exames.rota) }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    Icons.Default.Schedule,
                    contentDescription = "Contador de exame",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Faltam $diasParaExame dias para seu exame de faixa marrom",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.weight(1f))
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
