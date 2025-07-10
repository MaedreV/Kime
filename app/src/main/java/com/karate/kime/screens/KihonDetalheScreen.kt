package com.karate.kime.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.karate.kime.R
import com.karate.kime.model.Tecnica

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KihonDetalheScreen(
    tecnica: Tecnica,
    navController: NavHostController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tecnica.titulo) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Foto do golpe
            Image(
                painter = painterResource(
                    id = when (tecnica.id) {
                        "kihon1" -> R.drawable.age_uke
                        "kihon2" -> R.drawable.age_uke
                        "kihon3" -> R.drawable.age_uke
                        "kihon4" -> R.drawable.age_uke
                        "kihon5" -> R.drawable.age_uke
                        else     -> R.drawable.age_uke
                    }
                ),
                contentDescription = tecnica.titulo,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(16.dp))

            // Descrição
            Text(
                tecnica.descricao,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(24.dp))

            // Vídeo de demonstração (placeholder)
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(
                        id = when (tecnica.id) {
                            "kihon1" -> R.drawable.age_uke
                            "kihon2" -> R.drawable.age_uke
                            "kihon3" -> R.drawable.age_uke
                            "kihon4" -> R.drawable.age_uke
                            "kihon5" -> R.drawable.age_uke
                            else     -> R.drawable.age_uke
                        }
                    ),
                    contentDescription = "Vídeo de demonstração",
                    modifier = Modifier
                        .size(128.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Reproduzir vídeo",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
