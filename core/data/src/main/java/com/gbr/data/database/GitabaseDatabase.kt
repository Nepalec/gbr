package com.gbr.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gbr.data.database.dao.BookDao
import com.gbr.data.database.dao.ChapterDao
import com.gbr.data.database.dao.ImageDao
import com.gbr.data.database.dao.MeaningDao
import com.gbr.data.database.dao.SongDao
import com.gbr.data.database.dao.TextDao
import com.gbr.data.database.dao.TextNumDao
import com.gbr.data.database.entity.Book
import com.gbr.data.database.entity.Chapter
import com.gbr.data.database.entity.Eind
import com.gbr.data.database.entity.Image
import com.gbr.data.database.entity.ImageNum
import com.gbr.data.database.entity.LetterByTopic
import com.gbr.data.database.entity.Link
import com.gbr.data.database.entity.Meaning
import com.gbr.data.database.entity.Song
import com.gbr.data.database.entity.Text
import com.gbr.data.database.entity.TextNum
import com.gbr.data.database.entity.TextRef

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
