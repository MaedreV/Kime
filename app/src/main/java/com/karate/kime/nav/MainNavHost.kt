package com.karate.kime.nav

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavHostController
import com.karate.kime.MainViewModel
import com.karate.kime.screens.*
import com.karate.kime.model.Tecnica

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

        // Detalhe de uma faixa (Kihon/Kata)
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
            val tec = vm.tecnicas.find { it.titulo == titulo }
            if (tec != null) {
                KataDetalheScreen(tec, navController)
            } else {
                Text("Kata não encontrado", modifier = Modifier.padding(16.dp))
            }
        }

        // Detalhe de Golpe (Kihon)
        composable(
            route = "kihon/detalhe/{titulo}",
            arguments = listOf(navArgument("titulo") { type = NavType.StringType })
        ) { backStack ->
            val titulo = backStack.arguments?.getString("titulo") ?: return@composable
            val tec = vm.tecnicas.find { it.titulo == titulo }
            if (tec != null) {
                KihonDetalheScreen(tec, navController)
            } else {
                Text("Golpe não encontrado", modifier = Modifier.padding(16.dp))
            }
        }
    }
}
