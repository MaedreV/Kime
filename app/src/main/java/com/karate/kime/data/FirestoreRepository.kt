// app/src/main/java/com/karate/kime/data/FirestoreRepo.kt
package com.karate.kime.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.karate.kime.model.RequisitosExame
import com.karate.kime.model.Tecnica
import kotlinx.coroutines.tasks.await

private const val TAG = "FirestoreRepo"

private fun normalizeKey(key: String?): String {
    if (key == null) return ""
    return key.trim().lowercase().replace(Regex("[^a-z0-9]"), "")
}

object FirestoreRepo {
    private val db = Firebase.firestore
    private const val TECNICAS_COLL = "Técnicas"
    private const val REQUISITOS_COLL = "requisitos"
    private const val KATAS_COLL = "Katas"
    private const val USERS_COLL = "users"


    suspend fun fetchTecnicas(): List<Tecnica> {
        Log.d(TAG, "fetchTecnicas: lendo coleção $TECNICAS_COLL")
        val snapshot = db.collection(TECNICAS_COLL).get().await()
        Log.d(TAG, "fetchTecnicas: documentos encontrados = ${snapshot.size()}")
        return snapshot.documents.mapNotNull { doc ->
            try {
                Log.d(TAG, "DOC RAW (${doc.id}) -> ${doc.data}")
                val raw = doc.data ?: emptyMap<String, Any?>()
                val normalized: Map<String, Any?> = raw.entries.associate { (k, v) ->
                    normalizeKey(k) to v
                }

                val titulo = (normalized["titulo"] ?: normalized["title"])?.toString()
                    ?: run {
                        Log.w(TAG, "Documento ${doc.id} sem campo 'titulo', ignorando")
                        return@mapNotNull null
                    }
                val descricao = (normalized["descricao"] ?: normalized["description"] ?: "")?.toString() ?: ""
                val videoUrl = (normalized["videourl"] ?: "")?.toString() ?: ""
                val imageUrl = (normalized["imageurl"] ?: normalized["image_url"] ?: "")?.toString() ?: ""

                Log.d(TAG, "Documento mapeado -> id=${doc.id} titulo='$titulo' videoUrl='$videoUrl' imageUrl='$imageUrl'")

                Tecnica(
                    id = doc.id,
                    titulo = titulo,
                    descricao = descricao,
                    videoUrl = videoUrl,
                    imageUrl = imageUrl
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
                val raw = doc.data ?: emptyMap<String, Any?>()
                val normalized = raw.entries.associate { (k, v) -> normalizeKey(k) to v }

                val faixaId = doc.id
                val nome = (normalized["nome"] ?: normalized["name"] ?: faixaId)?.toString() ?: faixaId
                val cor = (normalized["cor"] ?: "#FFFFFF")?.toString() ?: "#FFFFFF"
                val kihonRaw = (normalized["kihon"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
                val kataRaw = (normalized["kata"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
                RequisitosExame(faixaId = faixaId, nome = nome, cor = cor, kihon = kihonRaw, kata = kataRaw)
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
        val raw = doc.data ?: emptyMap<String, Any?>()
        val normalized = raw.entries.associate { (k, v) -> normalizeKey(k) to v }

        val nome = (normalized["nome"] ?: normalized["name"] ?: faixaId)?.toString() ?: faixaId
        val cor = (normalized["cor"] ?: "#FFFFFF")?.toString() ?: "#FFFFFF"
        val kihon = (normalized["kihon"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
        val kata = (normalized["kata"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
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
            val raw = doc.data ?: emptyMap<String, Any?>()
            val normalized = raw.entries.associate { (k, v) -> normalizeKey(k) to v }
            Log.d(TAG, "fetchTecnicaById NORMALIZED (${doc.id}) -> keys=${normalized.keys}")

            val titulo = (normalized["titulo"] ?: normalized["title"])?.toString() ?: ""
            val descricao = (normalized["descricao"] ?: normalized["description"])?.toString() ?: ""
            val videoUrl = (normalized["videourl"] ?: "")?.toString() ?: ""
            val imageUrl = (normalized["imageurl"] ?: normalized["image_url"] ?: "")?.toString() ?: ""

            Log.d(TAG, "fetchTecnicaById: id=${doc.id} titulo='$titulo' videoUrl='$videoUrl' imageUrl='$imageUrl'")
            return Tecnica(id = doc.id, titulo = titulo, descricao = descricao, videoUrl = videoUrl, imageUrl = imageUrl)
        } catch (e: Exception) {
            Log.e(TAG, "Erro fetchTecnicaById($id): ${e.message}", e)
            return null
        }
    }


    suspend fun fetchKataSequence(kataId: String): List<Map<String, String>>? {
        suspend fun parseSequenceFromDoc(coll: String, docId: String): List<Map<String,String>>? {
            val doc = db.collection(coll).document(docId).get().await()
            if (!doc.exists()) {
                Log.d(TAG, "fetchKataSequence: documento $docId NÃO existe em $coll")
                return null
            }
            Log.d(TAG, "fetchKataSequence RAW ($coll/$docId) -> ${doc.data}")
            val rawList = doc.get("sequence") as? List<*>
            if (rawList == null) {
                Log.d(TAG, "fetchKataSequence: campo 'sequence' ausente em $coll/$docId")
                return emptyList()
            }

            val mapped = rawList.mapNotNull { item ->
                when (item) {
                    is String -> mapOf("techid" to item)
                    is Map<*, *> -> {
                        item.mapNotNull { (k, v) ->
                            val key = k?.toString() ?: return@mapNotNull null
                            key.lowercase().replace(Regex("[^a-z0-9]"), "") to (v?.toString() ?: "")
                        }.toMap()
                    }
                    else -> null
                }
            }
            Log.d(TAG, "fetchKataSequence: parsed size=${mapped.size} from $coll/$docId")
            return mapped
        }

        return try {
            val fromTecnicas = parseSequenceFromDoc(TECNICAS_COLL, kataId)
            if (fromTecnicas != null && fromTecnicas.isNotEmpty()) {
                Log.d(TAG, "fetchKataSequence: encontrou sequence em $TECNICAS_COLL/$kataId")
                return fromTecnicas
            }
            val fromKatas = parseSequenceFromDoc(KATAS_COLL, kataId)
            if (fromKatas != null) {
                Log.d(TAG, "fetchKataSequence: resultado de $KATAS_COLL/$kataId size=${fromKatas.size}")
                return fromKatas
            }
            Log.d(TAG, "fetchKataSequence: não encontrou sequence em nenhuma coleção para id=$kataId")
            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Erro fetchKataSequence($kataId): ${e.message}", e)
            null
        }
    }

    suspend fun saveUser(id: String, nome: String, email: String): Boolean {
        return try {
            val map = mapOf("nome" to nome, "email" to email)
            db.collection(USERS_COLL).document(id).set(map).await()
            Log.d(TAG, "saveUserProfile: salvo id=$id name='$nome'")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Erro saveUserProfile: ${e.message}", e)
            false
        }
    }

    suspend fun fetchUser(id: String): com.karate.kime.model.User? {
        return try {
            val doc = db.collection(USERS_COLL).document(id).get().await()
            if (!doc.exists()) {
                Log.w(TAG, "fetchUserProfile: id=$id não encontrado")
                return null
            }
            val raw = doc.data ?: emptyMap<String,Any?>()
            val nome = (raw["nome"] ?: "").toString()
            val email = (raw["email"] ?: "").toString()
            Log.d(TAG, "fetchUserProfile: id=$id -> nome='$nome' email='$email'")
            com.karate.kime.model.User(id = id, nome = nome, email = email)
        } catch (e: Exception) {
            Log.e(TAG, "Erro fetchUserProfile: ${e.message}", e)
            null
        }
    }
}
