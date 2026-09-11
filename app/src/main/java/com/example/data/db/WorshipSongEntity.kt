package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "worship_songs")
data class WorshipSongEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val artist: String,
    val durationSeconds: Int,
    val keyTone: String,
    val bpm: Int,
    val chords: String, // Comma-separated chord sequence, e.g. "C, G, D, Am, F"
    val isSample: Boolean = false,
    val audioUri: String? = null,
    val fileSizeMb: Float = 4.2f,
    val addedDate: Long = System.currentTimeMillis()
)
