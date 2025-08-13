package com.karate.kime.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.karate.kime.model.RequisitosExame
import com.karate.kime.model.Tecnica
import kotlinx.coroutines.tasks.await

private const val TAG = "FirestoreRepo"

object FirestoreRepo {
    private val db = Firebase.firestore
    private const val TECNICAS_COLL = "Técnicas"
    private const val REQUISITOS_COLL = "requisitos"

    suspend fun fetchTecnicas(): List<Tecnica> {
        Log.d(TAG, "fetchTecnicas: lendo coleção $TECNICAS_COLL")
        val snapshot = db.collection(TECNICAS_COLL).get().await()
        Log.d(TAG, "fetchTecnicas: documentos encontrados = ${snapshot.size()}")
        return snapshot.documents.mapNotNull { doc ->
            try {
                Log.d(TAG, "DOC RAW (${doc.id}) -> ${doc.data}")
                val id = doc.id
                val titulo = (doc.get("titulo") ?: doc.getString("titulo"))?.toString()
                    ?: run {
                        Log.w(TAG, "Documento $id sem campo 'titulo', ignorando")
                        return@mapNotNull null
                    }
                val descricao = (doc.get("descricao") ?: doc.getString("descricao"))?.toString() ?: ""
                val videoUrl = (doc.get("videoUrl") ?: doc.getString("videoUrl") ?: doc.get("videoURL") ?: doc.getString("videoURL") ?: doc.get("videourl"))?.toString()
                    ?: ""
                val imageUrl = (doc.get("imageUrl") ?: doc.getString("imageUrl") ?: doc.get("imageURL"))?.toString() ?: ""

                Log.d(TAG, "Documento mapeado -> id=$id titulo='$titulo' videoUrl='$videoUrl' imageUrl='$imageUrl'")

                Tecnica(
                    id = id,
                    titulo = titulo,
                    descricao = descricao,
                    videoUrl = videoUrl
                )
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao mapear doc ${doc.id}: ${e.message}", e)
                null
            }
        }
    }

    suspend fun fetchRequisitos(): Map<String, RequisitosExame> {
        val snapshot = db.collection(REQUISITOS_COLL).get().await()
        val list = snapshot.documents.mapNotNull { doc ->
            try {
                Log.d(TAG, "REQ RAW (${doc.id}) -> ${doc.data}")
                val faixaId = doc.id
                val nome = doc.getString("nome") ?: faixaId
                val cor = doc.getString("cor") ?: "#FFFFFF"
                val kihon = (doc.get("kihon") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
                val kata = (doc.get("kata") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
                RequisitosExame(faixaId = faixaId, nome = nome, cor = cor, kihon = kihon, kata = kata)
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao mapear requisito ${doc.id}: ${e.message}", e)
                null
            }
        }
        return list.associateBy { it.faixaId }
    }

    suspend fun fetchRequisitoById(faixaId: String): RequisitosExame? {
        val doc = db.collection(REQUISITOS_COLL).document(faixaId).get().await()
        if (!doc.exists()) {
            Log.w(TAG, "fetchRequisitoById: documento $faixaId não existe")
            return null
        }
        Log.d(TAG, "REQ BY ID RAW (${doc.id}) -> ${doc.data}")
        val nome = doc.getString("nome") ?: faixaId
        val cor = doc.getString("cor") ?: "#FFFFFF"
        val kihon = (doc.get("kihon") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
        val kata = (doc.get("kata") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
        return RequisitosExame(faixaId = faixaId, nome = nome, cor = cor, kihon = kihon, kata = kata)
    }

    suspend fun fetchTecnicaById(id: String): Tecnica? {
        try {
            val doc = db.collection(TECNICAS_COLL).document(id).get().await()
            if (!doc.exists()) {
                Log.w(TAG, "fetchTecnicaById: documento $id não existe")
                return null
            }
            Log.d(TAG, "fetchTecnicaById RAW (${doc.id}) -> ${doc.data}")
            val titulo = (doc.get("titulo") ?: doc.getString("titulo"))?.toString() ?: ""
            val descricao = (doc.get("descricao") ?: doc.getString("descricao"))?.toString() ?: ""
            val videoUrl = (doc.get("videoUrl") ?: doc.getString("videoUrl") ?: doc.get("videoURL") ?: doc.getString("videoURL") ?: doc.get("videourl"))?.toString() ?: ""
            val imageUrl = (doc.get("imageUrl") ?: doc.getString("imageUrl"))?.toString() ?: ""
            Log.d(TAG, "fetchTecnicaById: id=${doc.id} titulo='$titulo' videoUrl='$videoUrl' imageUrl='$imageUrl'")
            return Tecnica(id = doc.id, titulo = titulo, descricao = descricao, videoUrl = videoUrl)
        } catch (e: Exception) {
            Log.e(TAG, "Erro fetchTecnicaById($id): ${e.message}", e)
            return null
        }
    }
}
