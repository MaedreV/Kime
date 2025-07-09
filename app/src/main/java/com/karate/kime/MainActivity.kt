package com.karate.kime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.karate.kime.nav.BottomNavBar
import com.karate.kime.nav.MainNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm: MainViewModel = viewModel()
            val navController = rememberNavController()
            Scaffold(
                bottomBar = { BottomNavBar(navController) }
            ) { innerPadding ->
                MainNavHost(navController, vm)
            }
        }
    }
}