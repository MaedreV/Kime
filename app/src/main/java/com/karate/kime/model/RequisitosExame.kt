package com.karate.kime.model

import androidx.compose.ui.graphics.Color

data class RequisitosExame(
    val faixaId: String = "",
    val nome:    String = "",
    val cor:     String = "#FFFFFF",
    val kihon:   List<String> = emptyList(),
    val kata:    List<String> = emptyList()
)