package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exported_worship_songs")
data class ExportedSongEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val artist: String,
    val originalKey: String,
    val exportedKey: String,
    val transposeSemitones: Int,
    val bpm: Int,
    val chords: String,
    val vocalVolume: Float = 0.85f,
    val drumsVolume: Float = 0.80f,
    val bassVolume: Float = 0.80f,
    val padVolume: Float = 0.90f,
    val guitarVolume: Float = 0.75f,
    val ambientVolume: Float = 0.85f,
    val swellLevel: Float = 0.75f,
    val exportedDate: Long = System.currentTimeMillis(),
    val notes: String = "Exportado via J-MUSIC"
)
