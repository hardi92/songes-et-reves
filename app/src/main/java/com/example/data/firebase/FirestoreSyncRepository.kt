package com.example.data.firebase

import android.content.Context
import com.example.R
import com.example.data.model.DreamRecord
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreSyncRepository(private val context: Context) {

    // Resolves named database ID from string resource as required by skill guidelines
    private val db: FirebaseFirestore by lazy {
        val dbId = context.getString(R.string.firestore_database_id)
        FirebaseFirestore.getInstance(dbId)
    }

    suspend fun uploadRecord(userId: String, record: DreamRecord) {
        if (userId.isBlank()) return
        val docId = record.id.toString()
        val data = mapOf(
            "title" to record.title,
            "category" to record.category,
            "subCategory" to record.subCategory,
            "rawAudioText" to record.rawAudioText,
            "biblicalInterpretation" to record.biblicalInterpretation,
            "biblicalVerses" to record.biblicalVerses,
            "africanPerspective" to record.africanPerspective,
            "spiritualCounsel" to record.spiritualCounsel,
            "prayersAndActions" to record.prayersAndActions,
            "keySymbols" to record.keySymbols,
            "isFavorite" to record.isFavorite,
            "isEncrypted" to record.isEncrypted,
            "timestamp" to record.timestamp,
            "userId" to userId
        )

        db.collection("users")
            .document(userId)
            .collection("records")
            .document(docId)
            .set(data)
            .await()
    }

    fun observeRemoteRecords(userId: String): Flow<List<DreamRecord>> = callbackFlow {
        if (userId.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener: ListenerRegistration = db.collection("users")
            .document(userId)
            .collection("records")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val records = snapshot?.documents?.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    val id = doc.id.toLongOrNull() ?: 0L
                    DreamRecord(
                        id = id,
                        timestamp = (data["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                        rawAudioText = data["rawAudioText"] as? String ?: "",
                        title = data["title"] as? String ?: "",
                        category = data["category"] as? String ?: "REVE",
                        subCategory = data["subCategory"] as? String ?: "",
                        biblicalInterpretation = data["biblicalInterpretation"] as? String ?: "",
                        biblicalVerses = data["biblicalVerses"] as? String ?: "",
                        africanPerspective = data["africanPerspective"] as? String ?: "",
                        spiritualCounsel = data["spiritualCounsel"] as? String ?: "",
                        prayersAndActions = data["prayersAndActions"] as? String ?: "",
                        keySymbols = data["keySymbols"] as? String ?: "",
                        isFavorite = data["isFavorite"] as? Boolean ?: false,
                        isEncrypted = data["isEncrypted"] as? Boolean ?: true,
                        syncStatus = "SYNCED_CLOUD"
                    )
                } ?: emptyList()

                trySend(records)
            }

        awaitClose { listener.remove() }
    }

    suspend fun deleteRemoteRecord(userId: String, recordId: Long) {
        if (userId.isBlank()) return
        db.collection("users")
            .document(userId)
            .collection("records")
            .document(recordId.toString())
            .delete()
            .await()
    }
}
