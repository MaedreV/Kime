// app/src/main/java/com/karate/kime/screens/KataDetalheScreen.kt
package com.karate.kime.screens

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "KataDetalhe"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KataDetalheScreen(
    titulo: String,
    vm: MainViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val tecnicas = vm.tecnicas
    val found = remember(titulo, tecnicas) { tecnicas.find { it.titulo == titulo } }
    if (found == null) {
        Text("Kata não encontrado", Modifier.padding(16.dp))
        return
    }

    var tec by remember { mutableStateOf(found) }
    var bannerUrl by remember { mutableStateOf((tec.imageUrl ?: "").trim()) }

    LaunchedEffect(tec.id) {
        try {
            val refreshed = vm.fetchTecnicaById(tec.id)
            if (refreshed != null) {
                tec = refreshed
                bannerUrl = (refreshed.imageUrl ?: "").trim()
            }
            if (bannerUrl.isBlank()) {
                val seq = vm.fetchKataSequence(tec.id)
                if (!seq.isNullOrEmpty()) {
                    val first = seq.firstOrNull()
                    val techId = (first?.get("techid") ?: first?.get("techId") ?: "").trim()
                    if (techId.isNotBlank()) {
                        val refTech = vm.fetchTecnicaById(techId)
                        if (refTech != null) {
                            val refImage = (refTech.imageUrl ?: "").trim()
                            if (refImage.isNotBlank()) {
                                bannerUrl = refImage
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro no LaunchedEffect do KataDetalhe: ${e.message}", e)
        }
    }

    val videoId = (tec.videoUrl ?: "").trim()
    LaunchedEffect(videoId) { Log.d(TAG, "videoId detectado para ${tec.id}: '$videoId'") }
    val snippetState = produceState<Snippet?>(initialValue = null, videoId) {
        value = if (videoId.isNotBlank()) {
            try {
                vm.fetchVideoSnippet(videoId)
            } catch (e: Exception) {
                Log.e(TAG, "Erro fetchVideoSnippet: ${e.message}", e)
                null
            }
        } else null
    }

    val snackbarHostState = remember { SnackbarHostState() }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(errorMsg) { errorMsg?.let { snackbarHostState.showSnackbar(it) } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tec.titulo, maxLines = 1, overflow = TextOverflow.Ellipsis) },
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
            LiquidGlassCard(modifier = Modifier.fillMaxWidth(), corner = 12.dp) {
                if (bannerUrl.isNotBlank()) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(context).data(bannerUrl).crossfade(true).build(),
                        contentDescription = "${tec.titulo} banner",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    ) {
                        when (painter.state) {
                            is coil.compose.AsyncImagePainter.State.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                            is coil.compose.AsyncImagePainter.State.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Banner indisponível", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                            else -> SubcomposeAsyncImageContent()
                        }
                    }
                } else {
                    Box(Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                        Text("Banner não disponível", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Text(tec.titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(tec.descricao.ifBlank { "Sem descrição cadastrada." }, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(12.dp))

            Text("Sequência de Movimentos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))

            val sequenceState = produceState<List<Map<String,String>>?>(initialValue = null, tec.id) {
                value = try {
                    val s = vm.fetchKataSequence(tec.id)
                    Log.d(TAG, "fetchKataSequence(${tec.id}) -> ${s?.size ?: "null"}")
                    s
                } catch (e: Exception) {
                    Log.e(TAG, "Erro fetchKataSequence: ${e.message}", e)
                    null
                }
            }

            val seq = sequenceState.value
            if (seq == null) {
                Box(Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (seq.isEmpty()) {
                Text("Nenhuma sequência cadastrada.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                Column(Modifier.fillMaxWidth()) {
                    seq.forEachIndexed { idx, step ->
                        val stepNum = idx + 1
                        val techId = step["techid"] ?: step["techId"] ?: ""
                        val short = step["shortname"] ?: step["shortName"] ?: step["name"] ?: techId
                        val resolvedTitle = if (techId.isNotBlank()) vm.tecnicas.find { it.id == techId }?.titulo ?: short else short

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("$stepNum", modifier = Modifier.width(28.dp), fontWeight = FontWeight.Bold)
                                Spacer(Modifier.width(8.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(resolvedTitle, fontWeight = FontWeight.Medium)
                                    if ((step["stance"] ?: "").isNotBlank() || (step["target"] ?: "").isNotBlank()) {
                                        Text(listOfNotNull(step["stance"]?.takeIf { it.isNotBlank() }, step["target"]?.takeIf { it.isNotBlank() }).joinToString(" • "),
                                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                if (techId.isNotBlank()) {
                                    Text("Abrir", modifier = Modifier.clickable {
                                        val targetTitle = vm.tecnicas.find { it.id == techId }?.titulo ?: techId
                                        navController.navigate("kihon/detalhe/${Uri.encode(targetTitle)}")
                                    })
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Text("Demonstração", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))

            LiquidGlassCard(modifier = Modifier.fillMaxWidth(), corner = 12.dp) {
                Box(modifier = Modifier.height(220.dp)) {
                    if (videoId.isBlank()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Vídeo não cadastrado para este kata", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        YouTubeWebPlayer(
                            videoId = videoId,
                            modifier = Modifier.fillMaxSize(),
                            onError = { msg ->
                                Log.e(TAG, "Erro player: $msg")
                                errorMsg = "Erro ao carregar player"
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)) {
                OutlinedButton(
                    onClick = { if (videoId.isNotBlank()) openYoutube(context, videoId) },
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
                        context.startActivity(Intent.createChooser(sendIntent, "Compartilhar via"))
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
