package com.karate.kime

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.karate.kime.model.Exame
import com.karate.kime.model.StatusExame
import com.karate.kime.model.Tecnica
import java.time.LocalDate
import java.util.*

class MainViewModel : ViewModel() {
    // Simula lista de técnicas
    private val _tecnicas = mutableStateListOf(
        Tecnica("kihon1", "Oi-Zuki", "Golpe de punho básico", "VIDEO_ID_1"),
        Tecnica("kata1", "Heian Shodan", "Kata inicial de 21 movimentos", "VIDEO_ID_2"),
        Tecnica("kumite1", "Ippon Kumite", "Drill básico de combate", "VIDEO_ID_3"),
    )
    val tecnicas: List<Tecnica> get() = _tecnicas

    // Simula lista de exames
    private val _exames = mutableStateListOf<Exame>()
    val exames: List<Exame> get() = _exames

    fun agendarExame(exame: Exame) {
        _exames.add(exame)
    }

    fun concluirExame(id: String) {
        _exames.find { it.id == id }?.status = StatusExame.CONCLUIDO
    }
}