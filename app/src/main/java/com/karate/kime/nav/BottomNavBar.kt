package com.karate.kime.nav

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = bottomNavItems
    NavigationBar {
        val entry = navController.currentBackStackEntryAsState().value
        val rotaAtual = entry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icone, contentDescription = item.titulo) },
                label = { Text(item.titulo, fontSize = 12.sp) },
                selected = (rotaAtual == item.rota.rota),
                onClick = {
                    navController.navigate(item.rota.rota) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}