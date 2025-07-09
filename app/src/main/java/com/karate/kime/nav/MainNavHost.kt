package com.karate.kime.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.karate.kime.MainViewModel
import com.karate.kime.screens.*

@Composable
fun MainNavHost(navController: NavHostController, vm: MainViewModel) {
    NavHost(navController, startDestination = Rota.Biblioteca.rota) {
        composable(Rota.Biblioteca.rota) { BibliotecaScreen(vm, navController) }
        composable(Rota.Glossario.rota) { GlossarioScreen() }
        composable(Rota.Exames.rota) { ExamesScreen(vm, navController) }
        composable(Rota.Historia.rota) { HistoriaScreen() }
        composable(Rota.Perfil.rota) { PerfilScreen() }
    }
}