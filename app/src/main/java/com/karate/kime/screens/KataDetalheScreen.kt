package com.karate.kime.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.karate.kime.R
import com.karate.kime.model.Tecnica

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KataDetalheScreen(
    tecnica: Tecnica,
    navController: NavHostController
) {
    var favorito by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tecnica.titulo) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { favorito = !favorito }) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Favorito",
                            tint = if (favorito) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Banner de imagem (placeholder)
            item {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }
            // Descrição
            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    tecnica.descricao,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            // Sequência de Movimentos
            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    "Sequência de Movimentos",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            itemsIndexed(listOf("Posição Inicial", "Gedan Barai", "Oi-zuki", "Age-uke")) { index, passo ->
                var aberto by remember { mutableStateOf(false) }
                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { aberto = !aberto },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "${index + 1}. ",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                passo,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        if (aberto) {
                            Spacer(Modifier.height(8.dp))
                            Text("Descrição detalhada do passo $passo", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            // Vídeo de demonstração (placeholder)
            item {
                Spacer(Modifier.height(16.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(16.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground ),
                        contentDescription = "Reproduzir vídeo",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

