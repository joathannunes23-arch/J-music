package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SetlistDao {
    @Query("SELECT * FROM worship_setlists ORDER BY dateEpoch DESC")
    fun getAllSetlists(): Flow<List<SetlistEntity>>

    @Query("SELECT * FROM worship_setlists ORDER BY dateEpoch DESC")
    suspend fun getAllSetlistsSync(): List<SetlistEntity>

    @Query("SELECT * FROM worship_setlists WHERE id = :id LIMIT 1")
    suspend fun getSetlistById(id: Long): SetlistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetlist(setlist: SetlistEntity): Long

    @Update
    suspend fun updateSetlist(setlist: SetlistEntity)

    @Delete
    suspend fun deleteSetlist(setlist: SetlistEntity)

    @Query("DELETE FROM worship_setlists WHERE id = :id")
    suspend fun deleteSetlistById(id: Long)

    // Setlist Items
    @Query("SELECT * FROM worship_setlist_items WHERE setlistId = :setlistId ORDER BY orderIndex ASC")
    fun getItemsForSetlist(setlistId: Long): Flow<List<SetlistItemEntity>>

    @Query("SELECT * FROM worship_setlist_items WHERE setlistId = :setlistId ORDER BY orderIndex ASC")
    suspend fun getItemsForSetlistSync(setlistId: Long): List<SetlistItemEntity>

    @Query("SELECT COUNT(*) FROM worship_setlist_items WHERE setlistId = :setlistId")
    suspend fun getItemCount(setlistId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetlistItem(item: SetlistItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetlistItems(items: List<SetlistItemEntity>)

    @Update
    suspend fun updateSetlistItem(item: SetlistItemEntity)

    @Delete
    suspend fun deleteSetlistItem(item: SetlistItemEntity)

    @Query("DELETE FROM worship_setlist_items WHERE setlistId = :setlistId")
    suspend fun deleteItemsForSetlist(setlistId: Long)
}
