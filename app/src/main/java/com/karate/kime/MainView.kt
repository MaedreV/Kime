// app/src/main/java/com/karate/kime/MainViewModel.kt
package com.karate.kime

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.karate.kime.data.FirestoreRepo
import com.karate.kime.data.youtube.Snippet
import com.karate.kime.data.youtube.YouTubeClient
import com.karate.kime.model.RequisitosExame
import com.karate.kime.model.Tecnica
import com.karate.kime.model.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

private const val TAG = "MainViewModel"

class MainViewModel : ViewModel() {

    // Técnicas
    private val _tecnicas = mutableStateListOf<Tecnica>()
    val tecnicas: List<Tecnica> get() = _tecnicas

    // YouTube
    private val ytService = YouTubeClient.create()
    private val apiKey = BuildConfig.YOUTUBE_API_KEY

    // Requisitos por faixa
    private val _requisitos = MutableStateFlow<Map<String, RequisitosExame>>(emptyMap())
    val requisitos: StateFlow<Map<String, RequisitosExame>> = _requisitos.asStateFlow()

    // Firebase
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Usuário
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        // Carregar as teacnias
        viewModelScope.launch {
            try {
                val list = FirestoreRepo.fetchTecnicas()
                _tecnicas.clear()
                _tecnicas.addAll(list)
                Log.d(TAG, "Técnicas carregadas: ${_tecnicas.size}")
                _tecnicas.forEach { t ->
                    Log.d(TAG, "tec loaded -> id=${t.id} titulo='${t.titulo}' videoUrl='${t.videoUrl}' imageUrl='${t.imageUrl}'")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao carregar tecnicas: ${e.message}", e)
            }
        }

        // Carregar requisitos
        viewModelScope.launch {
            try {
                val map = FirestoreRepo.fetchRequisitos()
                _requisitos.value = map
                Log.d(TAG, "Requisitos carregados: ${map.keys}")
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao carregar requisitos: ${e.message}", e)
            }
        }

        //auth state
        auth.addAuthStateListener { firebaseAuth ->
            val fu = firebaseAuth.currentUser
            if (fu != null) {
                // obtém perfil salvo em users/{uid} (se existir) para pegar 'nome'
                viewModelScope.launch {
                    try {
                        val docSnap = firestore.collection("users").document(fu.uid).get().await()
                        val nomeFromDb = if (docSnap.exists()) docSnap.getString("nome") else null
                        val nome = nomeFromDb ?: fu.displayName
                        _currentUser.value = User(id = fu.uid, nome = nome, email = fu.email)
                        Log.d(TAG, "Auth estado: user logado uid=${fu.uid} nome=$nome email=${fu.email}")
                    } catch (e: Exception) {
                        // fallback com dados do auth
                        _currentUser.value = User(id = fu.uid, nome = fu.displayName, email = fu.email)
                        Log.e(TAG, "Erro carregar perfil do Firestore: ${e.message}", e)
                    }
                }
            } else {
                _currentUser.value = null
                Log.d(TAG, "Auth estado: sem usuário logado")
            }
        }
    }

    fun getRequisitos(faixaId: String): RequisitosExame? = _requisitos.value[faixaId]

    fun updateRequisitos(requisitosExame: RequisitosExame) {
        val copy = _requisitos.value.toMutableMap()
        copy[requisitosExame.faixaId] = requisitosExame
        _requisitos.value = copy
    }

    // busca snippet do youtube
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
            Log.d(TAG, "fetchKataSequence($kataId) -> size=${seq?.size ?: "null"}")
            seq
        } catch (e: Exception) {
            Log.e(TAG, "Erro fetchKataSequence: ${e.message}", e)
            null
        }
    }


    fun signIn(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            onResult(false, "Preencha e-mail e senha")
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message ?: "Falha ao autenticar")
                }
            }
    }


    fun signUp(name: String, email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            onResult(false, "Preencha todos os campos")
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val fu = auth.currentUser
                    try {
                        fu?.let { user ->
                            val req = UserProfileChangeRequest.Builder().setDisplayName(name).build()
                            user.updateProfile(req)
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Falha ao setDisplayName: ${e.message}")
                    }

                    val uid = fu?.uid
                    if (uid != null) {
                        val map = hashMapOf<String, Any>("nome" to name, "email" to email)
                        firestore.collection("users").document(uid)
                            .set(map)
                            .addOnSuccessListener {
                                Log.d(TAG, "Cadastro: usuario salvo em users/$uid")
                                onResult(true, null)
                            }
                            .addOnFailureListener { ex ->
                                Log.w(TAG, "Cadastro salvo firestore falhou: ${ex.message}")
                                onResult(true, "Usuário criado, mas falha ao salvar perfil: ${ex.message}")
                            }
                    } else {
                        onResult(true, null)
                    }
                } else {
                    onResult(false, task.exception?.message ?: "Falha ao criar usuário")
                }
            }
    }

    fun signOut() {
        auth.signOut()
        _currentUser.value = null
    }
}
