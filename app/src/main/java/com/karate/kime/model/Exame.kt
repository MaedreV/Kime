package com.karate.kime.model

import java.time.LocalDate

enum class StatusExame { PENDENTE, CONCLUIDO }

data class Exame(
    val id: String,
    val data: LocalDate,
    val faixaId: String,
    var status: StatusExame = StatusExame.PENDENTE
)