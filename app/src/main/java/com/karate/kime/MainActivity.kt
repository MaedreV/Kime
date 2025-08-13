package com.karate.kime

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.karate.kime.nav.BottomNavBar
import com.karate.kime.nav.MainNavHost

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

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
