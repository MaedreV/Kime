package com.karate.kime.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.karate.kime.MainViewModel
import com.karate.kime.data.youtube.Snippet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KataDetalheScreen(
    titulo: String,
    vm: MainViewModel,
    navController: NavHostController
) {
    val tecnicas = vm.tecnicas
    val tec = remember(titulo, tecnicas) {
        tecnicas.find { it.titulo == titulo }
    } ?: run {
        Text("Kata não encontrado", Modifier.padding(16.dp))
        return
    }

    val videoId = tec.videoUrl

    val snippetState = produceState<Snippet?>(initialValue = null, videoId) {
        value = vm.fetchVideoSnippet(videoId!!)
    }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tec.titulo) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            Spacer(Modifier.height(12.dp))

            YouTubeWebPlayer(
                videoId = videoId!!,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = snippetState.value?.title ?: tec.titulo,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = snippetState.value?.description ?: tec.descricao,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(18.dp))

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { if (videoId!!.isNotBlank()) openYoutube(context, videoId) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Abrir no YouTube")
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

private fun openYoutube(context: android.content.Context, videoId: String) {
    if (videoId.isBlank()) return
    val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId"))
    try {
        context.startActivity(appIntent)
    } catch (ex: ActivityNotFoundException) {
        context.startActivity(webIntent)
    }
}
