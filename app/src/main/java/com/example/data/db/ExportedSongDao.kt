package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExportedSongDao {
    @Query("SELECT * FROM exported_worship_songs ORDER BY exportedDate DESC")
    fun getAllExportedSongs(): Flow<List<ExportedSongEntity>>

    @Query("SELECT * FROM exported_worship_songs WHERE id = :id")
    suspend fun getExportedSongById(id: Long): ExportedSongEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExportedSong(song: ExportedSongEntity): Long

    @Delete
    suspend fun deleteExportedSong(song: ExportedSongEntity)

    @Query("SELECT COUNT(*) FROM exported_worship_songs")
    suspend fun getExportedCount(): Int
}
