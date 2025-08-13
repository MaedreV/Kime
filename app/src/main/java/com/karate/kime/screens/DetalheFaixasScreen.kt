package com.karate.kime.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.SportsMartialArts
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.karate.kime.MainViewModel
import com.karate.kime.model.RequisitosExame
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheFaixasScreen(
    vm: MainViewModel,
    faixaId: String,
    navController: NavHostController
) {
    val requisitosMap by vm.requisitos.collectAsState(initial = emptyMap())

    val req: RequisitosExame? = requisitosMap[faixaId]
    if (req == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Faixa") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                )
            }
        ) { inner ->
            Column(
                Modifier
                    .padding(inner)
                    .padding(16.dp)
            ) {
                Text("Faixa não encontrada.", style = MaterialTheme.typography.bodyMedium)
            }
        }
        return
    }

    val bgColor = try {
        Color(android.graphics.Color.parseColor(req.cor))
    } catch (e: Exception) {
        Color.Gray
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = bgColor,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(bgColor)
                    .padding(14.dp)
            ) {
                Text(
                    "Requisitos do Exame — ${req.nome}",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Complete todos os itens abaixo. Toque para ver o detalhe de cada técnica.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = {  },
                    label = { Text("${req.kihon.size} Kihon") },
                    leadingIcon = { Icon(Icons.Outlined.SportsMartialArts, contentDescription = null) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = bgColor.copy(alpha = 0.12f))
                )
                AssistChip(
                    onClick = {  },
                    label = { Text("${req.kata.size} Kata") },
                    leadingIcon = { Icon(Icons.Outlined.Book, contentDescription = null) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = bgColor.copy(alpha = 0.12f))
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // kihon saecton
            SectionCard(
                title = "Kihon",
                iconTint = MaterialTheme.colorScheme.primary
            ) {
                if (req.kihon.isEmpty()) {
                    Text("Nenhum kihon cadastrado.", Modifier.padding(12.dp))
                } else {
                    req.kihon.forEachIndexed { idx, golpe ->
                        ItemRow(
                            label = golpe,
                            onClick = {
                                val encoded = Uri.encode(golpe)
                                navController.navigate("kihon/detalhe/$encoded")
                            }
                        )
                        if (idx < req.kihon.size - 1) Divider()
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // kata section
            SectionCard(
                title = "Kata",
                iconTint = MaterialTheme.colorScheme.primary
            ) {
                if (req.kata.isEmpty()) {
                    Text("Nenhum kata cadastrado.", Modifier.padding(12.dp))
                } else {
                    req.kata.forEachIndexed { idx, kata ->
                        ItemRow(
                            label = kata,
                            onClick = {
                                val encoded = Uri.encode(kata)
                                navController.navigate("kata/detalhe/$encoded")
                            }
                        )
                        if (idx < req.kata.size - 1) Divider()
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            OutlinedButton(
                onClick = { /* vm.agendarExame(LocalDate.now().plusDays(30), faixaId) */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agendar exame (exemplo)")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ItemRow(label: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp
        )
        Icon(Icons.Default.ArrowForward, contentDescription = null)
    }
}

/** Card com título e área de conteúdo */
@Composable
private fun SectionCard(title: String, iconTint: Color = Color.Unspecified, content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.SportsMartialArts, contentDescription = null, tint = iconTint)
            Spacer(modifier = Modifier.width(10.dp))
            Text(title, style = MaterialTheme.typography.titleMedium)
        }
        Divider()
        Column(Modifier.padding(0.dp)) {
            content()
        }
    }
}
