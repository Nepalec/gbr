package com.gbr.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.gbr.data.database.dao.*
import com.gbr.data.database.entity.*

@Database(
    entities = [
        Book::class,
        Chapter::class,
        Song::class,
        Text::class,
        TextNum::class,
        TextRef::class,
        Image::class,
        ImageNum::class,
        Meaning::class,
        Eind::class,
        Link::class,
        LetterByTopic::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GitabaseDatabase : RoomDatabase() {
    
    abstract fun bookDao(): BookDao
    abstract fun chapterDao(): ChapterDao
    abstract fun songDao(): SongDao
    abstract fun textDao(): TextDao
    abstract fun textNumDao(): TextNumDao
    abstract fun imageDao(): ImageDao
    abstract fun meaningDao(): MeaningDao
    
    companion object {
        @Volatile
        private var INSTANCE: GitabaseDatabase? = null
        
        fun getDatabase(context: Context): GitabaseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GitabaseDatabase::class.java,
                    "gitabase_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
