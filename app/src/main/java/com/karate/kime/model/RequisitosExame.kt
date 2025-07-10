package com.karate.kime.model

import androidx.compose.ui.graphics.Color

data class RequisitosExame(
    val faixaId: String,
    val nome: String,
    val cor: Color,
    val kihon: List<String>,
    val kata: List<String>,
   // val kumite: List<String>
)