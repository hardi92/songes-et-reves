package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dream_records")
data class DreamRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val rawAudioText: String,
    val title: String,
    val category: String, // "REVE" or "PROPHETIE"
    val subCategory: String, // e.g. "Songe divin", "Vision nocturne", "Avertissement prophétique", "Orientation"
    val biblicalInterpretation: String,
    val biblicalVerses: String,
    val africanPerspective: String,
    val spiritualCounsel: String,
    val prayersAndActions: String,
    val keySymbols: String, // Comma separated or list
    val isFavorite: Boolean = false,
    val isEncrypted: Boolean = true,
    val syncStatus: String = "LOCAL_PROTECTED" // "LOCAL_PROTECTED", "SYNCED", "OFFLINE"
)
