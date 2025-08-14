// app/src/main/java/com/karate/kime/MainViewModel.kt
package com.karate.kime

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.karate.kime.data.FirestoreRepo
import com.karate.kime.data.youtube.Snippet
import com.karate.kime.data.youtube.YouTubeClient
import com.karate.kime.model.RequisitosExame
import com.karate.kime.model.Tecnica
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "MainViewModel"

class MainViewModel : ViewModel() {

    // Técnicas
    private val _tecnicas = mutableStateListOf<Tecnica>()
    val tecnicas: List<Tecnica> get() = _tecnicas

    // YouTube
    private val ytService = YouTubeClient.create()
    private val apiKey = BuildConfig.YOUTUBE_API_KEY

    private val _requisitos = MutableStateFlow<Map<String, RequisitosExame>>(emptyMap())
    val requisitos: StateFlow<Map<String, RequisitosExame>> = _requisitos.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val list = FirestoreRepo.fetchTecnicas()
                _tecnicas.clear()
                _tecnicas.addAll(list)
                Log.d(TAG, "Técnicas carregadas: ${_tecnicas.size}")
                _tecnicas.forEach { t -> Log.d(TAG, "tec loaded -> id=${t.id} titulo='${t.titulo}' videoUrl='${t.videoUrl}' imageUrl='${t.imageUrl}'") }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao carregar tecnicas: ${e.message}", e)
            }
        }

        viewModelScope.launch {
            try {
                val map = FirestoreRepo.fetchRequisitos()
                _requisitos.value = map
                Log.d(TAG, "Requisitos carregados: ${map.keys}")
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao carregar requisitos: ${e.message}", e)
            }
        }
    }

    fun getRequisitos(faixaId: String): RequisitosExame? = _requisitos.value[faixaId]

    fun updateRequisitos(requisitosExame: RequisitosExame) {
        val copy = _requisitos.value.toMutableMap()
        copy[requisitosExame.faixaId] = requisitosExame
        _requisitos.value = copy
    }

    suspend fun fetchVideoSnippet(videoId: String): Snippet? {
        if (videoId.isBlank()) return null
        return try {
            val resp = ytService.getVideos(id = videoId, key = apiKey)
            if (resp.items.isNotEmpty()) resp.items[0].snippet else null
        } catch (e: Exception) {
            Log.e(TAG, "Erro YouTube API: ${e.message}", e)
            null
        }
    }


    suspend fun fetchTecnicaById(id: String): Tecnica? {
        Log.d(TAG, "fetchTecnicaById: solicitando id=$id")
        try {
            val t = FirestoreRepo.fetchTecnicaById(id)
            Log.d(TAG, "fetchTecnicaById: recebido do repo -> $t")
            if (t != null) {
                viewModelScope.launch {
                    val idx = _tecnicas.indexOfFirst { it.id == t.id }
                    Log.d(TAG, "fetchTecnicaById: idx=$idx antes -> ${if (idx >= 0) _tecnicas[idx] else "nao-existente"}")
                    if (idx >= 0) {
                        _tecnicas[idx] = t
                        Log.d(TAG, "fetchTecnicaById: substituiu técnica index=$idx id=${t.id} videoUrl='${t.videoUrl}' imageUrl='${t.imageUrl}'")
                    } else {
                        _tecnicas.add(t)
                        Log.d(TAG, "fetchTecnicaById: adicionou técnica id=${t.id} videoUrl='${t.videoUrl}' imageUrl='${t.imageUrl}'")
                    }
                }
            } else {
                Log.w(TAG, "fetchTecnicaById: técnica $id não encontrada no Firestore")
            }
            return t
        } catch (e: Exception) {
            Log.e(TAG, "Erro fetchTecnicaById: ${e.message}", e)
            return null
        }
    }


    suspend fun fetchKataSequence(kataId: String): List<Map<String, String>>? {
        return try {
            val seq = FirestoreRepo.fetchKataSequence(kataId)
            android.util.Log.d(TAG, "fetchKataSequence($kataId) -> size=${seq?.size ?: "null"}")
            seq
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Erro fetchKataSequence: ${e.message}", e)
            null
        }
    }
}
