package com.example.data.repository

import com.example.data.db.ExportedSongDao
import com.example.data.db.ExportedSongEntity
import com.example.data.db.WorshipSongDao
import com.example.data.db.WorshipSongEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class WorshipSongRepository(
    private val dao: WorshipSongDao,
    private val exportedDao: ExportedSongDao? = null
) {
    val allSongs: Flow<List<WorshipSongEntity>> = dao.getAllSongs()
    val allExportedSongs: Flow<List<ExportedSongEntity>> = exportedDao?.getAllExportedSongs() ?: flowOf(emptyList())

    suspend fun insertSong(song: WorshipSongEntity): Long {
        return dao.insertSong(song)
    }

    suspend fun deleteSong(song: WorshipSongEntity) {
        dao.deleteSong(song)
    }

    suspend fun getSongById(id: Long): WorshipSongEntity? {
        return dao.getSongById(id)
    }

    suspend fun insertExportedSong(song: ExportedSongEntity): Long {
        return exportedDao?.insertExportedSong(song) ?: 0L
    }

    suspend fun deleteExportedSong(song: ExportedSongEntity) {
        exportedDao?.deleteExportedSong(song)
    }

    suspend fun seedInitialSampleSongsIfEmpty() {
        if (dao.getSongCount() == 0) {
            val samples = listOf(
                WorshipSongEntity(
                    title = "A Ele a Glória (Live Worship)",
                    artist = "Gabriela Rocha / Diante do Trono",
                    durationSeconds = 248,
                    keyTone = "C Maior",
                    bpm = 72,
                    chords = "C, G, Am, F, Em, Dm",
                    isSample = true,
                    fileSizeMb = 5.8f
                ),
                WorshipSongEntity(
                    title = "Oceans (Where Feet May Fail)",
                    artist = "Hillsong UNITED",
                    durationSeconds = 295,
                    keyTone = "D Maior",
                    bpm = 66,
                    chords = "D, A, Em, G, Am",
                    isSample = true,
                    fileSizeMb = 7.1f
                ),
                WorshipSongEntity(
                    title = "Bondade de Deus (Goodness of God)",
                    artist = "Bethel Music / Isaías Saad",
                    durationSeconds = 312,
                    keyTone = "G Maior",
                    bpm = 70,
                    chords = "G, C, D, Em, Am",
                    isSample = true,
                    fileSizeMb = 6.4f
                ),
                WorshipSongEntity(
                    title = "Rompendo em Fé (Worship Pad Mix)",
                    artist = "Comunidade Zona Sul",
                    durationSeconds = 220,
                    keyTone = "F Maior",
                    bpm = 74,
                    chords = "F, C, Dm, Bb, Gm, Am",
                    isSample = true,
                    fileSizeMb = 5.2f
                )
            )
            for (sample in samples) {
                dao.insertSong(sample)
            }
        }

        // Seed an initial exported song if none exists yet
        if (exportedDao != null && exportedDao.getExportedCount() == 0) {
            exportedDao.insertExportedSong(
                ExportedSongEntity(
                    title = "A Ele a Glória (Master Worship Mix)",
                    artist = "Gabriela Rocha",
                    originalKey = "C Maior",
                    exportedKey = "D Maior",
                    transposeSemitones = 2,
                    bpm = 72,
                    chords = "D, A, Bm, G, F#m, Em",
                    vocalVolume = 0.90f,
                    drumsVolume = 0.70f,
                    bassVolume = 0.85f,
                    padVolume = 0.95f,
                    guitarVolume = 0.80f,
                    ambientVolume = 0.90f,
                    swellLevel = 0.82f,
                    notes = "Mix com Pad Shimmer e Tom D (+2 st) para voz masculina"
                )
            )
        }
    }
}
