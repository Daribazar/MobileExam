package com.example.word.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Word::class], version = 1, exportSchema = false)
abstract class WordDB: RoomDatabase() {
    abstract fun WordDAO(): WordDAO

    companion object {
        @Volatile
        private var Instance: WordDB? = null

        fun getDatabase(context: Context): WordDB {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, WordDB::class.java, "wordDB")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}