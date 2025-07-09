package com.karate.kime.model

import java.time.LocalDate

enum class StatusExame { PENDENTE, CONCLUIDO }

data class Exame(
    val id: String,
    val data: LocalDate,
    val faixa: String,
    val requisitosKihon: List<String>,
    val requisitosKata: List<String>,
    val requisitosKumite: List<String>,
    var status: StatusExame = StatusExame.PENDENTE
)