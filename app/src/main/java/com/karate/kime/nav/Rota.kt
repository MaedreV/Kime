package com.karate.kime.nav

sealed class Rota(val rota: String) {
    object Biblioteca: Rota("biblioteca")
    object Glossario: Rota("glossario")
    object Exames: Rota("exames")
    object Historia: Rota("historia")
    object Perfil: Rota("perfil")
}