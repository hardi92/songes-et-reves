package com.example.data.repository

import com.example.ai.InterpretationResult
import com.example.ai.SpiritualInterpreter
import com.example.data.dao.DreamDao
import com.example.data.model.DreamRecord
import com.example.security.CryptoManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DreamRepository(private val dao: DreamDao) {

    val allRecords: Flow<List<DreamRecord>> = dao.getAllRecords().map { list ->
        list.map { record ->
            record.copy(
                rawAudioText = CryptoManager.decrypt(record.rawAudioText),
                biblicalInterpretation = CryptoManager.decrypt(record.biblicalInterpretation),
                africanPerspective = CryptoManager.decrypt(record.africanPerspective),
                spiritualCounsel = CryptoManager.decrypt(record.spiritualCounsel),
                prayersAndActions = CryptoManager.decrypt(record.prayersAndActions)
            )
        }
    }

    val favoriteRecords: Flow<List<DreamRecord>> = dao.getFavoriteRecords().map { list ->
        list.map { record ->
            record.copy(
                rawAudioText = CryptoManager.decrypt(record.rawAudioText),
                biblicalInterpretation = CryptoManager.decrypt(record.biblicalInterpretation),
                africanPerspective = CryptoManager.decrypt(record.africanPerspective),
                spiritualCounsel = CryptoManager.decrypt(record.spiritualCounsel),
                prayersAndActions = CryptoManager.decrypt(record.prayersAndActions)
            )
        }
    }

    fun getRecordsByCategory(category: String): Flow<List<DreamRecord>> = dao.getRecordsByCategory(category).map { list ->
        list.map { record ->
            record.copy(
                rawAudioText = CryptoManager.decrypt(record.rawAudioText),
                biblicalInterpretation = CryptoManager.decrypt(record.biblicalInterpretation),
                africanPerspective = CryptoManager.decrypt(record.africanPerspective),
                spiritualCounsel = CryptoManager.decrypt(record.spiritualCounsel),
                prayersAndActions = CryptoManager.decrypt(record.prayersAndActions)
            )
        }
    }

    fun getRecordById(id: Long): Flow<DreamRecord?> = dao.getRecordById(id).map { record ->
        record?.copy(
            rawAudioText = CryptoManager.decrypt(record.rawAudioText),
            biblicalInterpretation = CryptoManager.decrypt(record.biblicalInterpretation),
            africanPerspective = CryptoManager.decrypt(record.africanPerspective),
            spiritualCounsel = CryptoManager.decrypt(record.spiritualCounsel),
            prayersAndActions = CryptoManager.decrypt(record.prayersAndActions)
        )
    }

    fun searchRecords(query: String): Flow<List<DreamRecord>> = dao.searchRecords(query).map { list ->
        list.map { record ->
            record.copy(
                rawAudioText = CryptoManager.decrypt(record.rawAudioText),
                biblicalInterpretation = CryptoManager.decrypt(record.biblicalInterpretation),
                africanPerspective = CryptoManager.decrypt(record.africanPerspective),
                spiritualCounsel = CryptoManager.decrypt(record.spiritualCounsel),
                prayersAndActions = CryptoManager.decrypt(record.prayersAndActions)
            )
        }
    }

    suspend fun saveInterpretation(
        rawText: String,
        interpretation: InterpretationResult,
        syncStatus: String = "SYNCED_CLOUD_SECURE"
    ): Long {
        val encryptedRecord = DreamRecord(
            rawAudioText = CryptoManager.encrypt(rawText),
            title = interpretation.title,
            category = interpretation.category,
            subCategory = interpretation.subCategory,
            biblicalInterpretation = CryptoManager.encrypt(interpretation.biblicalInterpretation),
            biblicalVerses = interpretation.biblicalVerses,
            africanPerspective = CryptoManager.encrypt(interpretation.africanPerspective),
            spiritualCounsel = CryptoManager.encrypt(interpretation.spiritualCounsel),
            prayersAndActions = CryptoManager.encrypt(interpretation.prayersAndActions),
            keySymbols = interpretation.keySymbols.joinToString(", "),
            isFavorite = false,
            isEncrypted = true,
            syncStatus = syncStatus
        )
        return dao.insertRecord(encryptedRecord)
    }

    suspend fun toggleFavorite(record: DreamRecord) {
        val reEncrypted = record.copy(
            rawAudioText = CryptoManager.encrypt(record.rawAudioText),
            biblicalInterpretation = CryptoManager.encrypt(record.biblicalInterpretation),
            africanPerspective = CryptoManager.encrypt(record.africanPerspective),
            spiritualCounsel = CryptoManager.encrypt(record.spiritualCounsel),
            prayersAndActions = CryptoManager.encrypt(record.prayersAndActions),
            isFavorite = !record.isFavorite
        )
        dao.updateRecord(reEncrypted)
    }

    suspend fun deleteRecord(record: DreamRecord) {
        dao.deleteById(record.id)
    }

    suspend fun checkAndSeedInitialData() {
        val initialSonge = "J'ai rêvé que je me tenais devant un très grand fleuve aux eaux calmes et limpides. De l'autre côté se dressait un majestueux baobab aux racines profondes baignées de lumière. Un aigle royal est descendu du ciel et s'est posé sur mon épaule droite sans me faire de mal, tenant dans son bec une clé scintillante en or."
        val interpSonge = SpiritualInterpreter.fallbackSpiritualInterpretation(initialSonge)
        
        val initialProphetie = "Voici la parole qui m'a été révélée dans une vision intérieure : 'Prépare-toi, car la saison de la sécheresse s'achève. Un vent nouveau va souffler sur ta famille et relever ce qui était abattu. Ne crains point les clameurs du dehors, affermis tes pas dans la justice et le pardon.'"
        val interpProphetie = SpiritualInterpreter.fallbackSpiritualInterpretation(initialProphetie)

        saveInterpretation(initialSonge, interpSonge, "SYNCED_CLOUD_SECURE")
        saveInterpretation(initialProphetie, interpProphetie, "SYNCED_CLOUD_SECURE")
    }
}
