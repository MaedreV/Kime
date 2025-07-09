package com.karate.kime.components

import androidx.compose.runtime.Composable

@Composable
fun ProgressoGeral(
    filtroAtivo: String?,
    onFiltroChange: (String) -> Unit
) {
    ProgressSection(
        activeFilter = filtroAtivo,
        setActiveFilter = onFiltroChange
    )
}