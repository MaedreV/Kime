package com.karate.kime.model

import androidx.compose.ui.graphics.Color

data class RequisitosExame(
    val faixaId: String,          // "verde"
    val nome: String,             // "Faixa Verde"
    val cor: Color,               // Color.Green
    val kihon: List<String>,
    val kata: List<String>,
    val kumite: List<String>
)