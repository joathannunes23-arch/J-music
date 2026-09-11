package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorshipSongDao {
    @Query("SELECT * FROM worship_songs ORDER BY addedDate DESC")
    fun getAllSongs(): Flow<List<WorshipSongEntity>>

    @Query("SELECT * FROM worship_songs WHERE id = :id")
    suspend fun getSongById(id: Long): WorshipSongEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: WorshipSongEntity): Long

    @Delete
    suspend fun deleteSong(song: WorshipSongEntity)

    @Query("SELECT COUNT(*) FROM worship_songs")
    suspend fun getSongCount(): Int
}
