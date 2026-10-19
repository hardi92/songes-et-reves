package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DreamRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface DreamDao {
    @Query("SELECT * FROM dream_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<DreamRecord>>

    @Query("SELECT * FROM dream_records WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteRecords(): Flow<List<DreamRecord>>

    @Query("SELECT * FROM dream_records WHERE category = :category ORDER BY timestamp DESC")
    fun getRecordsByCategory(category: String): Flow<List<DreamRecord>>

    @Query("SELECT * FROM dream_records WHERE id = :id LIMIT 1")
    fun getRecordById(id: Long): Flow<DreamRecord?>

    @Query("SELECT * FROM dream_records WHERE title LIKE '%' || :query || '%' OR rawAudioText LIKE '%' || :query || '%' OR keySymbols LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchRecords(query: String): Flow<List<DreamRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: DreamRecord): Long

    @Update
    suspend fun updateRecord(record: DreamRecord)

    @Delete
    suspend fun deleteRecord(record: DreamRecord)

    @Query("DELETE FROM dream_records WHERE id = :id")
    suspend fun deleteById(id: Long)
}
