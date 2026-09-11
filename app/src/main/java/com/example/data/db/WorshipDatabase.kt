package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        WorshipSongEntity::class,
        ExportedSongEntity::class,
        SetlistEntity::class,
        SetlistItemEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class WorshipDatabase : RoomDatabase() {
    abstract fun worshipSongDao(): WorshipSongDao
    abstract fun exportedSongDao(): ExportedSongDao
    abstract fun setlistDao(): SetlistDao

    companion object {
        @Volatile
        private var INSTANCE: WorshipDatabase? = null

        fun getDatabase(context: Context): WorshipDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorshipDatabase::class.java,
                    "worship_moises_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
