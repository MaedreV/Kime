package com.karate.kime.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.mediumTopAppBarColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.SportsMartialArts
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.karate.kime.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheFaixasScreen(
    vm: MainViewModel,
    faixaId: String,
    navController: NavHostController
) {
    val req = vm.getRequisitos(faixaId)
        ?: run {
            Text("Faixa não encontrada", Modifier.padding(16.dp))
            return
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(req.nome) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = mediumTopAppBarColors(
                    containerColor = req.cor,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            // Banner "Requisitos do Exame"
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(req.cor)
                    .padding(vertical = 12.dp, horizontal = 16.dp)
            ) {
                Text(
                    "Requisitos do Exame",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            Text(
                "Complete todos os requisitos abaixo",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(req.cor.copy(alpha = 0.1f))
                    .padding(12.dp)
            )

            Spacer(Modifier.height(16.dp))

            // Seção Kihon
            SectionCard(
                title = "Kihon",
                icon = Icons.Outlined.SportsMartialArts,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                req.kihon.forEach { golpe ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("kihon/detalhe/$golpe") }
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            golpe,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Divider()
                }
            }

            Spacer(Modifier.height(16.dp))

            // Seção Kata
            SectionCard(
                title = "Kata",
                icon = Icons.Outlined.Book,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                req.kata.forEach { kata ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("kata/detalhe/$kata") }
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            kata,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Divider()
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: ImageVector,
    color: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(color)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .background(color)
                .padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Text(title, style = MaterialTheme.typography.titleMedium)
        }
        Divider()
        Column {
            content()
        }
    }
}
