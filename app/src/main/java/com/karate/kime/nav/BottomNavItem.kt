package com.karate.kime.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val titulo: String,
    val icone: ImageVector,
    val rota: Rota
)

val bottomNavItems = listOf(
    BottomNavItem("Home", Icons.Default.Home, Rota.Home),
  //  BottomNavItem("Glossário", Icons.Default.Info, Rota.Glossario),
    BottomNavItem("Exames", Icons.Default.DateRange, Rota.Exames),
    BottomNavItem("Perfil", Icons.Default.Person, Rota.Perfil),
)