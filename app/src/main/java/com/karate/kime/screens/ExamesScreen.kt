package com.karate.kime.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.karate.kime.MainViewModel
import com.karate.kime.nav.BottomNavBar
import com.karate.kime.nav.Rota

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExamesScreen(vm: MainViewModel, nav: NavHostController) {
    val requisitosMap by vm.requisitos.collectAsState()

    val faixas = requisitosMap.values.toList()

    Log.i("ExamesUI", "Renderizando ExamesScreen — ${faixas.size} faixas")

    Scaffold(
        bottomBar = { BottomNavBar(nav) }
    ) { padding ->
        LazyColumn(
            Modifier
                //.padding(padding)
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            items(faixas) { req ->
                Log.i("ExamesUI", "Exibindo faixa: ${req.nome} (id=${req.faixaId}, cor=${req.cor})")

                val bgColor = runCatching {
                    Color(android.graphics.Color.parseColor(req.cor))
                }.getOrDefault(Color.LightGray)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            val destino = "${Rota.Exames.rota}/faixa/${req.faixaId}"
                            Log.i("NavDebug","Navegando para $destino")
                            nav.navigate(destino)
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = bgColor.copy(alpha = 1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = req.nome,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Detalhes da faixa"
                        )
                    }
                }
            }
        }
    }
}
