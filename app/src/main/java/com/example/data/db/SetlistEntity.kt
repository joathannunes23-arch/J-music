package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade para persistência de uma Setlist de Louvor.
 * Guarda informações do culto/reunião e a data do serviço.
 */
@Entity(tableName = "worship_setlists")
data class SetlistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String, // ex: "Culto de Domingo - Manhã", "Vigília de Louvor"
    val serviceDate: String = "Domingo 10:00",
    val description: String = "",
    val dateEpoch: Long = System.currentTimeMillis()
)

/**
 * Entidade para persistência de uma música dentro de uma Setlist.
 * Salva sequência ordenada, tom pré-definido, BPM e volumes dos canais de stems.
 */
@Entity(tableName = "worship_setlist_items")
data class SetlistItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val setlistId: Long,
    val songId: Long,
    val songTitle: String,
    val artist: String,
    val orderIndex: Int = 0,
    val keyTone: String = "C Maior",
    val transposeSemitones: Int = 0,
    val bpm: Int = 72,
    val masterVolume: Float = 0.85f,
    // Stems pre-set volumes
    val vocalVolume: Float = 0.85f,
    val drumsVolume: Float = 0.75f,
    val bassVolume: Float = 0.80f,
    val padVolume: Float = 0.90f,
    val guitarVolume: Float = 0.70f,
    val ambientVolume: Float = 0.85f,
    val notes: String = ""
)
