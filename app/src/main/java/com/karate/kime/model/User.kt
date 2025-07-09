package com.karate.kime.model

data class User(
    val id: String,
    val nome: String? = null,
    val email: String? = null
)