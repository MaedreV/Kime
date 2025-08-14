package com.karate.kime.nav

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.karate.kime.MainViewModel
import com.karate.kime.screens.DetalheFaixasScreen
import com.karate.kime.screens.ExamesScreen
import com.karate.kime.screens.HomeScreen
import com.karate.kime.screens.KataDetalheScreen
import com.karate.kime.screens.KihonDetalheScreen
import com.karate.kime.screens.LoginScreen
import com.karate.kime.screens.PerfilScreen
import com.karate.kime.screens.RegisterScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainNavHost(navController: NavHostController, vm: MainViewModel) {
    NavHost(navController, startDestination = Rota.Home.rota) {

        // Home
        composable(Rota.Home.rota) {
            HomeScreen(vm, navController)
        }

        // Exames
        composable(Rota.Exames.rota) {
            ExamesScreen(vm, navController)
        }

        // Detalhes de Faixa
        composable(
            route = "exames/faixa/{faixaId}",
            arguments = listOf(navArgument("faixaId") { type = NavType.StringType })
        ) { backStack ->
            val faixaId = backStack.arguments?.getString("faixaId") ?: return@composable
            DetalheFaixasScreen(vm, faixaId, navController)
        }

        // Perfil
        composable(Rota.Perfil.rota) {
            PerfilScreen(vm)
        }

        // Detalhe de Kata
        composable(
            route = "kata/detalhe/{titulo}",
            arguments = listOf(navArgument("titulo") { type = NavType.StringType })
        ) { backStack ->
            val titulo = backStack.arguments?.getString("titulo") ?: return@composable
            KataDetalheScreen(
                titulo = titulo,
                vm = vm,
                navController = navController
            )
        }

        // Detalhe de Kihon
        composable(
            route = "kihon/detalhe/{titulo}",
            arguments = listOf(navArgument("titulo") { type = NavType.StringType })
        ) { backStack ->
            val titulo = backStack.arguments?.getString("titulo") ?: return@composable
            KihonDetalheScreen(
                titulo = titulo,
                vm = vm,
                navController = navController
            )
        }

        composable("login") { LoginScreen(vm, navController) }
        composable("register") { RegisterScreen(vm, navController) }

    }
}
