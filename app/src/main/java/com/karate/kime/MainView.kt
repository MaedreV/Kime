package com.karate.kime

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.karate.kime.model.Exame
import com.karate.kime.model.RequisitosExame
import com.karate.kime.model.StatusExame
import com.karate.kime.model.Tecnica
import java.time.LocalDate
import java.util.*

class MainViewModel : ViewModel() {
    // 0) Lista simulada de técnicas (para detalhes de kata/golpe)
    private val _tecnicas = mutableStateListOf(
        Tecnica("kata1", "Heian Shodan", "Primeiro kata da série Heian.", "VIDEO_ID_1"),
        Tecnica("kata2", "Heian Nidan",  "Segundo kata da série Heian.",   "VIDEO_ID_2"),
        // ... adicione mais conforme desejar

        Tecnica("kihon1", "Oi-Zuki",   "Golpe de punho básico, projeção frontal.", "VIDEO_ID_OI_ZUKI"),
        Tecnica("kihon2", "Gedan Barai","Bloqueio baixo em zenkutsu‑dachi.",       "VIDEO_ID_GEDAN"),
        Tecnica("kihon3", "Age-Uke",   "Bloqueio ascendente para defender ataques de cima.", "VIDEO_ID_AGE_UKE"),
        Tecnica("kihon4", "Uchi‑Uke",  "Bloqueio interno para defender socos laterais.",   "VIDEO_ID_UCHI_UKE"),
        Tecnica("kihon5", "Soto‑Uke",  "Bloqueio externo para desvios laterais.",            "VIDEO_ID_SOTO_UKE"),
    )
    val tecnicas: List<Tecnica> get() = _tecnicas
    // 1) Map de requisitos padronizados para cada faixa
    private val requisitosMap = listOf(
        RequisitosExame(
            faixaId = "branca",
            nome    = "Faixa Branca",
            cor     = Color.White,
            kihon   = listOf("Oi-Zuki", "Gedan Barai"),
            kata    = listOf("Heian Shodan"),
          //  kumite  = listOf("Ippon Kumite Básico")
        ),
        RequisitosExame(
            faixaId = "verde",
            nome    = "Faixa Verde",
            cor     = Color.Green,
            kihon   = listOf("Oi-Zuki", "Gedan Barai","Gyaku-Zuki", "Age-Uke", "Uchi‑Uke", "Soto‑Uke"),
            kata    = listOf("Heian Nidan", "Heian Sandan", "Heian Yondan"),
         //   kumite  = listOf("Sanbon Kumite")
        )
        // …adicione as demais faixas aqui…
    ).associateBy { it.faixaId }

    // 2) Lista simulada de exames agendados
    private val _exames = mutableStateListOf<Exame>()
    val exames: List<Exame> get() = _exames

    /** Agendar um exame */
    fun agendarExame(data: LocalDate, faixaId: String) {
        val exame = Exame(
            id    = UUID.randomUUID().toString(),
            data  = data,
            faixaId = faixaId,
            status = StatusExame.PENDENTE
        )
        _exames.add(exame)
    }

    /** Marcar um exame como concluído */
    fun concluirExame(id: String) {
        _exames.find { it.id == id }?.status = StatusExame.CONCLUIDO
    }

    /** Retorna os requisitos para a faixa escolhida, ou null */
    fun getRequisitos(faixaId: String): RequisitosExame? =
        requisitosMap[faixaId.lowercase()]
}
