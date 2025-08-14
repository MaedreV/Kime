// app/src/main/java/com/karate/kime/screens/KihonDetalheScreen.kt
package com.karate.kime.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.karate.kime.MainViewModel
import com.karate.kime.data.youtube.Snippet
import com.karate.kime.util.openYoutube
import com.karate.kime.ui.theme.LiquidGlassCard

private const val TAG = "KihonDetalhe"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KihonDetalheScreen(
    titulo: String,
    vm: MainViewModel,
    navController: NavHostController
) {
    val tecnicas = vm.tecnicas
    val found = remember(titulo, tecnicas) { tecnicas.find { it.titulo == titulo } }

    if (found == null) {
        Text("Golpe não encontrado", Modifier.padding(16.dp))
        return
    }

    var tec by remember { mutableStateOf(found) }

    LaunchedEffect(tec.id) {
        if (tec.imageUrl.isBlank() || tec.videoUrl.isBlank()) {
            try {
                vm.fetchTecnicaById(tec.id)?.let { tec = it }
            } catch (_: Exception) { /* silêncio: UI mostra placeholder */ }
        }
    }

    val imageUrl = (tec.imageUrl ?: "").trim()
    val videoId = (tec.videoUrl ?: "").trim()

    val snippetState = produceState<Snippet?>(initialValue = null, videoId) {
        value = if (videoId.isNotBlank()) {
            try { vm.fetchVideoSnippet(videoId) } catch (_: Exception) { null }
        } else null
    }

    val ctx = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(errorMsg) { errorMsg?.let { snackbarHostState.showSnackbar(it) } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(tec.titulo, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.SemiBold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            LiquidGlassCard(modifier = Modifier.fillMaxWidth(), corner = 14.dp) {
                if (imageUrl.isNotBlank()) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "${tec.titulo} - imagem",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.Fit
                    ) {
                        when (painter.state) {
                            is coil.compose.AsyncImagePainter.State.Loading -> {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }
                            is coil.compose.AsyncImagePainter.State.Error -> {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Imagem indisponível", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(Modifier.height(6.dp))
                                        OutlinedButton(onClick = { /* Recompose retry: troque a chave se quiser forçar reload */ }) {
                                            Text("Tentar novamente")
                                        }
                                    }
                                }
                            }
                            else -> SubcomposeAsyncImageContent()
                        }
                    }
                } else {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Imagem não disponível", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            LiquidGlassCard(modifier = Modifier.fillMaxWidth(), corner = 12.dp) {
                Column(Modifier.padding(14.dp)) {
                    Text("Descrição", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(6.dp))
                    Text(text = tec.descricao.ifBlank { "Sem descrição cadastrada." }, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(Modifier.height(12.dp))

            Text("Como fazer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))

            LiquidGlassCard(modifier = Modifier.fillMaxWidth(), corner = 12.dp) {
                Box(Modifier.height(220.dp)) {
                    if (videoId.isBlank()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Vídeo não cadastrado para esta técnica", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        YouTubeWebPlayer(
                            videoId = videoId,
                            modifier = Modifier.fillMaxSize(),
                            onError = { errorMsg = "Erro ao carregar player" }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)) {
                OutlinedButton(
                    onClick = { if (videoId.isNotBlank()) openYoutube(ctx, videoId) },
                    enabled = videoId.isNotBlank()
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Abrir no YouTube")
                }

                OutlinedButton(onClick = {
                    if (videoId.isNotBlank()) {
                        val shareText = "Veja como fazer ${tec.titulo}: https://www.youtube.com/watch?v=$videoId"
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        ctx.startActivity(Intent.createChooser(sendIntent, "Compartilhar via"))
                    } else {
                        errorMsg = "Não há vídeo para compartilhar."
                    }
                }) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Compartilhar")
                }
            }

            errorMsg?.let {
                Spacer(Modifier.height(10.dp))
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(28.dp))
        }
    }
}
