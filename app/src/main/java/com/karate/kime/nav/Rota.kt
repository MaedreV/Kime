package com.karate.kime.nav

sealed class Rota(val rota: String) {
    object Home: Rota("home")
    object Exames: Rota("exames")
    object Perfil: Rota("perfil")
}